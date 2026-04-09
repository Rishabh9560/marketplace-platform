#!/bin/bash

# ============================================
# Marketplace Platform - Full Deployment Script
# ============================================
# 
# This script deploys the complete marketplace platform:
# - Backend API (vendor-service)
# - Frontend Dashboard (vendor-dashboard)
# - Database (PostgreSQL)
# - Cache (Redis)
# - Networking (Ingress, Services)
# - Monitoring (Prometheus)
# - Security (NetworkPolicy, RBAC)
#
# Usage: ./deploy.sh [namespace] [environment]
#

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE="${1:-marketplace}"
ENVIRONMENT="${2:-staging}"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo -e "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Marketplace Platform - Full Stack Deployment            ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "Environment: ${YELLOW}${ENVIRONMENT}${NC}"
echo -e "Namespace: ${YELLOW}${NAMESPACE}${NC}"
echo ""

# Functions
log_step() {
    echo -e "\n${BLUE}→ $1${NC}"
}

log_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

log_error() {
    echo -e "${RED}✗ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 is not installed"
        return 1
    fi
    return 0
}

verify_resources() {
    log_step "Verifying Kubernetes cluster resources..."
    
    # Check nodes
    NODES=$(kubectl get nodes -o jsonpath='{.items[*].metadata.name}')
    NODE_COUNT=$(echo $NODES | wc -w)
    
    if [ $NODE_COUNT -lt 3 ]; then
        log_warning "Cluster has only $NODE_COUNT nodes. Recommended: 3+ nodes"
    else
        log_success "Cluster has $NODE_COUNT nodes ✓"
    fi
    
    # Check storage classes
    if kubectl get storageclass | grep -q "fast-ssd"; then
        log_success "Storage class 'fast-ssd' found ✓"
    else
        log_warning "Storage class 'fast-ssd' not found. Using default storage."
    fi
    
    # Check ingress controller
    if kubectl get pods -n ingress-nginx 2>/dev/null | grep -q "ingress-nginx-controller"; then
        log_success "Nginx ingress controller found ✓"
    else
        log_warning "Nginx ingress controller not found. Installing..."
        helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
        helm repo update
        helm install nginx-ingress ingress-nginx/ingress-nginx \
            --namespace ingress-nginx --create-namespace \
            --set controller.service.type=LoadBalancer
        log_success "Nginx ingress controller installed ✓"
    fi
    
    # Check cert-manager
    if kubectl get pods -n cert-manager 2>/dev/null | grep -q "cert-manager"; then
        log_success "Cert-manager found ✓"
    else
        log_warning "Cert-manager not found. Installing..."
        kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
        kubectl wait --for=condition=ready pod -l app.kubernetes.io/instance=cert-manager -n cert-manager --timeout=300s
        log_success "Cert-manager installed ✓"
    fi
}

deploy_manifests() {
    log_step "Creating namespace with security policies..."
    kubectl apply -f "$SCRIPT_DIR/00-namespace.yaml"
    log_success "Namespace created ✓"
    
    log_step "Deploying application configuration..."
    kubectl apply -f "$SCRIPT_DIR/01-configmap.yaml"
    log_success "ConfigMaps deployed ✓"
    
    log_step "Setting up secrets (UPDATE THESE FOR PRODUCTION!)..."
    kubectl apply -f "$SCRIPT_DIR/02-secrets.yaml"
    log_warning "⚠️  REMINDER: Update secrets in 02-secrets.yaml for production!"
    log_success "Secrets created ✓"
    
    log_step "Creating storage volumes..."
    kubectl apply -f "$SCRIPT_DIR/03-storage.yaml"
    kubectl wait --for=condition=Available pvc/vendor-db-pvc -n $NAMESPACE --timeout=60s
    log_success "Storage configured ✓"
    
    log_step "Deploying PostgreSQL database..."
    kubectl apply -f "$SCRIPT_DIR/04-postgres.yaml"
    kubectl wait --for=condition=ready pod -l app=vendor-db -n $NAMESPACE --timeout=300s
    log_success "PostgreSQL deployed ✓"
    
    log_step "Deploying backend API service..."
    kubectl apply -f "$SCRIPT_DIR/05-deployment.yaml"
    kubectl wait --for=condition=available --timeout=300s deployment/vendor-service -n $NAMESPACE
    log_success "Backend API deployed ✓"
    
    log_step "Setting up service discovery..."
    kubectl apply -f "$SCRIPT_DIR/06-service.yaml"
    log_success "Services configured ✓"
    
    log_step "Deploying frontend dashboard..."
    kubectl apply -f "$SCRIPT_DIR/10-dashboard-deployment.yaml"
    kubectl apply -f "$SCRIPT_DIR/11-dashboard-service.yaml"
    kubectl apply -f "$SCRIPT_DIR/12-dashboard-config.yaml"
    kubectl wait --for=condition=available --timeout=300s deployment/vendor-dashboard -n $NAMESPACE
    log_success "Frontend deployed ✓"
    
    log_step "Configuring ingress and TLS..."
    kubectl apply -f "$SCRIPT_DIR/13-marketplace-ingress.yaml"
    kubectl wait --for=condition=ready certificate/marketplace-cert -n $NAMESPACE --timeout=600s 2>/dev/null || log_warning "Certificate still being issued..."
    log_success "Ingress configured ✓"
    
    log_step "Setting up auto-scaling..."
    kubectl apply -f "$SCRIPT_DIR/08-autoscaling.yaml"
    log_success "Auto-scaling configured ✓"
    
    log_step "Deploying monitoring (optional)..."
    if kubectl apply -f "$SCRIPT_DIR/09-monitoring.yaml" 2>/dev/null; then
        log_success "Monitoring deployed ✓"
    else
        log_warning "Monitoring requires Prometheus operator (optional)"
    fi
}

verify_deployment() {
    log_step "Verifying deployment status..."
    echo ""
    
    echo -e "${BLUE}Pods Status:${NC}"
    kubectl get pods -n $NAMESPACE -L app,tier
    echo ""
    
    echo -e "${BLUE}Services:${NC}"
    kubectl get svc -n $NAMESPACE
    echo ""
    
    echo -e "${BLUE}Ingress:${NC}"
    kubectl get ingress -n $NAMESPACE
    echo ""
    
    echo -e "${BLUE}Deployments:${NC}"
    kubectl get deployments -n $NAMESPACE
    echo ""
    
    # Wait for pods to be ready
    log_step "Waiting for all pods to be ready..."
    kubectl wait --for=condition=ready pod -l tier=backend -n $NAMESPACE --timeout=300s 2>/dev/null || true
    kubectl wait --for=condition=ready pod -l tier=frontend -n $NAMESPACE --timeout=300s 2>/dev/null || true
    
    # Check pod status
    BACKEND_READY=$(kubectl get deployment vendor-service -n $NAMESPACE -o jsonpath='{.status.readyReplicas}')
    FRONTEND_READY=$(kubectl get deployment vendor-dashboard -n $NAMESPACE -o jsonpath='{.status.readyReplicas}')
    
    if [ "$BACKEND_READY" -ge 1 ]; then
        log_success "Backend API is ready ($BACKEND_READY replicas) ✓"
    else
        log_error "Backend API is not ready"
    fi
    
    if [ "$FRONTEND_READY" -ge 1 ]; then
        log_success "Frontend dashboard is ready ($FRONTEND_READY replicas) ✓"
    else
        log_error "Frontend dashboard is not ready"
    fi
}

show_access_info() {
    log_step "Deployment complete! Access information:"
    echo ""
    
    if [ "$ENVIRONMENT" == "local" ]; then
        echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
        echo -e "${BLUE}║        Local Development Access        ║${NC}"
        echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "Frontend: ${GREEN}http://localhost:3000${NC}"
        echo -e "Backend:  ${GREEN}http://localhost:8080${NC}"
        echo -e "Database: ${GREEN}localhost:5432${NC}"
        echo -e "Redis:    ${GREEN}localhost:6379${NC}"
    else
        echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
        echo -e "${BLUE}║      Production Access (Update DNS)    ║${NC}"
        echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "Frontend: ${GREEN}https://vendor.marketplace.com${NC}"
        echo -e "Backend:  ${GREEN}https://api.vendor.marketplace.com${NC}"
        echo ""
        echo -e "${YELLOW}⚠ DNS must be configured to point to ingress controller IP${NC}"
        
        INGRESS_IP=$(kubectl get svc -n ingress-nginx ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "PENDING")
        echo -e "Ingress IP: ${YELLOW}${INGRESS_IP}${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║        Useful Commands                 ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "View logs:"
    echo -e "  ${YELLOW}kubectl logs -f deployment/vendor-service -n $NAMESPACE${NC}"
    echo -e "  ${YELLOW}kubectl logs -f deployment/vendor-dashboard -n $NAMESPACE${NC}"
    echo ""
    echo -e "Check pod status:"
    echo -e "  ${YELLOW}kubectl get pods -n $NAMESPACE${NC}"
    echo ""
    echo -e "Port forward (for local testing):"
    echo -e "  ${YELLOW}kubectl port-forward svc/vendor-service 8080:8080 -n $NAMESPACE${NC}"
    echo -e "  ${YELLOW}kubectl port-forward svc/vendor-dashboard 3000:80 -n $NAMESPACE${NC}"
    echo ""
    echo -e "Monitor scaling:"
    echo -e "  ${YELLOW}kubectl get hpa -n $NAMESPACE --watch${NC}"
    echo ""
    echo -e "View metrics:"
    echo -e "  ${YELLOW}kubectl top pods -n $NAMESPACE${NC}"
    echo ""
}

# Main execution
main() {
    echo -e "${BLUE}Step 1: Checking prerequisites${NC}"
    check_command kubectl || exit 1
    check_command helm || exit 1
    log_success "Prerequisites check passed ✓"
    echo ""
    
    verify_resources
    
    echo ""
    echo -e "${BLUE}Step 2: Deploying manifests${NC}"
    deploy_manifests
    
    echo ""
    echo -e "${BLUE}Step 3: Verifying deployment${NC}"
    verify_deployment
    
    echo ""
    show_access_info
    
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              Deployment Completed Successfully!             ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════════════╝${NC}"
}

# Run main function
main
