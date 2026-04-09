#!/bin/bash

# ============================================
# Marketplace Platform - Verification & Testing Script
# ============================================
#
# This script verifies the complete marketplace deployment:
# - Backend API health
# - Frontend service health
# - Database connectivity
# - Network policies
# - TLS certificates
# - Auto-scaling setup
# - Performance metrics
#
# Usage: ./verify.sh [namespace] [environment]
#

set -e

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuration
NAMESPACE="${1:-marketplace}"
ENVIRONMENT="${2:-staging}"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Counters
PASSED=0
FAILED=0
WARNINGS=0

# Functions
print_header() {
    echo ""
    echo -e "${CYAN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║ $1${NC}"
    echo -e "${CYAN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

test_pass() {
    echo -e "${GREEN}✓ $1${NC}"
    ((PASSED++))
}

test_fail() {
    echo -e "${RED}✗ $1${NC}"
    ((FAILED++))
}

test_warn() {
    echo -e "${YELLOW}⚠ $1${NC}"
    ((WARNINGS++))
}

check_pod_status() {
    local deployment=$1
    local min_replicas=${2:-1}
    
    local ready=$(kubectl get deployment $deployment -n $NAMESPACE -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo 0)
    
    if [ "$ready" -ge "$min_replicas" ]; then
        test_pass "$deployment: $ready replicas ready"
        return 0
    else
        test_fail "$deployment: Only $ready/$min_replicas replicas ready"
        return 1
    fi
}

check_service_endpoints() {
    local service=$1
    local expected_endpoints=${2:-1}
    
    local endpoints=$(kubectl get endpoints $service -n $NAMESPACE -o jsonpath='{.subsets[0].addresses[*].targetRef.name}' 2>/dev/null | wc -w)
    
    if [ "$endpoints" -ge "$expected_endpoints" ]; then
        test_pass "$service: $endpoints endpoints active"
        return 0
    else
        test_fail "$service: Only $endpoints/$expected_endpoints endpoints active"
        return 1
    fi
}

check_http_endpoint() {
    local url=$1
    local expected_code=${2:-200}
    
    local response=$(curl -s -o /dev/null -w "%{http_code}" "$url" -k --connect-timeout 5 || echo "000")
    
    if [ "$response" == "$expected_code" ]; then
        test_pass "HTTP endpoint $url: $response"
        return 0
    else
        test_fail "HTTP endpoint $url: Expected $expected_code, got $response"
        return 1
    fi
}

# Main verification
main() {
    print_header "Marketplace Platform Verification Suite"
    echo -e "Namespace: ${YELLOW}${NAMESPACE}${NC}"
    echo -e "Environment: ${YELLOW}${ENVIRONMENT}${NC}"
    
    # ==========================================
    # 1. CHECK CLUSTER CONNECTIVITY
    # ==========================================
    print_header "1. Cluster Connectivity"
    
    if kubectl cluster-info &>/dev/null; then
        test_pass "Kubernetes cluster reachable"
    else
        test_fail "Kubernetes cluster unreachable"
        exit 1
    fi
    
    if kubectl get namespace $NAMESPACE &>/dev/null; then
        test_pass "Namespace '$NAMESPACE' exists"
    else
        test_fail "Namespace '$NAMESPACE' not found"
        exit 1
    fi
    
    # ==========================================
    # 2. CHECK POD STATUS
    # ==========================================
    print_header "2. Pod Status"
    
    check_pod_status "vendor-service" 1 || true
    check_pod_status "vendor-dashboard" 1 || true
    
    # Database should have 1 pod (StatefulSet)
    DB_PODS=$(kubectl get pods -n $NAMESPACE -l app=vendor-db -o jsonpath='{.items[*].metadata.name}' | wc -w)
    if [ "$DB_PODS" -ge 1 ]; then
        test_pass "vendor-db: $DB_PODS pod(s) ready"
    else
        test_fail "vendor-db: No pods running"
    fi
    
    # ==========================================
    # 3. CHECK SERVICES
    # ==========================================
    print_header "3. Service Configuration"
    
    check_service_endpoints "vendor-service" 1 || true
    check_service_endpoints "vendor-dashboard" 1 || true
    
    # Check service types
    BACKEND_TYPE=$(kubectl get svc vendor-service -n $NAMESPACE -o jsonpath='{.spec.type}' 2>/dev/null || echo "Not Found")
    if [ "$BACKEND_TYPE" == "ClusterIP" ]; then
        test_pass "vendor-service: Type is ClusterIP"
    else
        test_warn "vendor-service: Type is $BACKEND_TYPE (expected ClusterIP)"
    fi
    
    FRONTEND_TYPE=$(kubectl get svc vendor-dashboard -n $NAMESPACE -o jsonpath='{.spec.type}' 2>/dev/null || echo "Not Found")
    if [ "$FRONTEND_TYPE" == "ClusterIP" ]; then
        test_pass "vendor-dashboard: Type is ClusterIP"
    else
        test_warn "vendor-dashboard: Type is $FRONTEND_TYPE (expected ClusterIP)"
    fi
    
    # ==========================================
    # 4. CHECK INGRESS
    # ==========================================
    print_header "4. Ingress Configuration"
    
    if kubectl get ingress marketplace-ingress -n $NAMESPACE &>/dev/null; then
        test_pass "Ingress 'marketplace-ingress' exists"
        
        # Check ingress rules
        RULES=$(kubectl get ingress marketplace-ingress -n $NAMESPACE -o jsonpath='{.spec.rules[*].host}' | wc -w)
        test_pass "Ingress has $RULES routing rules"
    else
        test_fail "Ingress 'marketplace-ingress' not found"
    fi
    
    # ==========================================
    # 5. CHECK TLS CERTIFICATES
    # ==========================================
    print_header "5. TLS Certificate Status"
    
    CERT_READY=$(kubectl get certificate marketplace-cert -n $NAMESPACE -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null || echo "Unknown")
    
    if [ "$CERT_READY" == "True" ]; then
        test_pass "TLS certificate 'marketplace-cert': Ready"
        
        CERT_EXPIRY=$(kubectl get certificate marketplace-cert -n $NAMESPACE -o jsonpath='{.status.notAfter}' 2>/dev/null)
        test_pass "Certificate expires: $CERT_EXPIRY"
    elif [ "$CERT_READY" == "False" ]; then
        test_warn "TLS certificate 'marketplace-cert': Not ready (may be in progress)"
    else
        test_fail "TLS certificate 'marketplace-cert': Unknown status"
    fi
    
    # ==========================================
    # 6. CHECK DATABASE
    # ==========================================
    print_header "6. Database Connectivity"
    
    # Try to exec into database pod
    DB_POD=$(kubectl get pods -n $NAMESPACE -l app=vendor-db -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
    
    if [ ! -z "$DB_POD" ]; then
        if kubectl exec -it $DB_POD -n $NAMESPACE -- psql -U postgres -c "SELECT 1" &>/dev/null; then
            test_pass "PostgreSQL database: Connection successful"
        else
            test_warn "PostgreSQL database: Cannot connect (may need password)"
        fi
        
        # Check storage
        STORAGE=$(kubectl get pvc vendor-db-pvc -n $NAMESPACE -o jsonpath='{.status.phase}' 2>/dev/null)
        if [ "$STORAGE" == "Bound" ]; then
            test_pass "Database PVC: Bound and ready"
        else
            test_warn "Database PVC: Status is $STORAGE"
        fi
    else
        test_fail "Database pod not found"
    fi
    
    # ==========================================
    # 7. CHECK CONFIGMAPS & SECRETS
    # ==========================================
    print_header "7. Configuration & Secrets"
    
    if kubectl get configmap dashboard-config -n $NAMESPACE &>/dev/null; then
        test_pass "ConfigMap 'dashboard-config' exists"
    else
        test_fail "ConfigMap 'dashboard-config' not found"
    fi
    
    if kubectl get configmap vendor-config -n $NAMESPACE &>/dev/null; then
        test_pass "ConfigMap 'vendor-config' exists"
    else
        test_fail "ConfigMap 'vendor-config' not found"
    fi
    
    SECRETS=$(kubectl get secrets -n $NAMESPACE | grep -c marketplace-secrets || echo 0)
    if [ "$SECRETS" -gt 0 ]; then
        test_pass "Secrets configured"
    else
        test_warn "No secrets found (may be using external secret management)"
    fi
    
    # ==========================================
    # 8. CHECK NETWORK POLICIES
    # ==========================================
    print_header "8. Network Policies"
    
    NETPOL_COUNT=$(kubectl get networkpolicies -n $NAMESPACE 2>/dev/null | wc -l)
    if [ "$NETPOL_COUNT" -gt 1 ]; then
        test_pass "Network policies: $((NETPOL_COUNT-1)) policies found"
    else
        test_warn "Network policies: No policies configured (consider adding)"
    fi
    
    # ==========================================
    # 9. CHECK RBAC
    # ==========================================
    print_header "9. RBAC Configuration"
    
    if kubectl get role vendor-service -n $NAMESPACE &>/dev/null; then
        test_pass "RBAC Role 'vendor-service' exists"
    else
        test_warn "RBAC Role 'vendor-service' not found"
    fi
    
    if kubectl get rolebinding vendor-service -n $NAMESPACE &>/dev/null; then
        test_pass "RBAC RoleBinding 'vendor-service' exists"
    else
        test_warn "RBAC RoleBinding 'vendor-service' not found"
    fi
    
    # ==========================================
    # 10. CHECK AUTO-SCALING
    # ==========================================
    print_header "10. Horizontal Pod AutoScaling (HPA)"
    
    if kubectl get hpa vendor-service -n $NAMESPACE &>/dev/null; then
        test_pass "HPA for vendor-service exists"
        
        MIN=$(kubectl get hpa vendor-service -n $NAMESPACE -o jsonpath='{.spec.minReplicas}')
        MAX=$(kubectl get hpa vendor-service -n $NAMESPACE -o jsonpath='{.spec.maxReplicas}')
        test_pass "HPA scaling range: $MIN - $MAX replicas"
    else
        test_warn "HPA for vendor-service not configured"
    fi
    
    if kubectl get hpa vendor-dashboard -n $NAMESPACE &>/dev/null; then
        test_pass "HPA for vendor-dashboard exists"
    else
        test_warn "HPA for vendor-dashboard not configured"
    fi
    
    # ==========================================
    # 11. CHECK RESOURCE QUOTAS
    # ==========================================
    print_header "11. Resource Quotas"
    
    if kubectl get resourcequota -n $NAMESPACE &>/dev/null; then
        test_pass "Resource quotas configured"
        kubectl describe resourcequota -n $NAMESPACE | grep -E "(pods|requests|limits)" || true
    else
        test_warn "No resource quotas configured"
    fi
    
    # ==========================================
    # 12. CHECK RESOURCE USAGE
    # ==========================================
    print_header "12. Actual Resource Usage"
    
    echo "Pod CPU/Memory usage:"
    kubectl top pods -n $NAMESPACE 2>/dev/null || test_warn "Metrics server not available"
    
    # ==========================================
    # 13. FUNCTIONAL TESTS (if endpoints available)
    # ==========================================
    print_header "13. Functional Tests"
    
    # Try local port-forward test
    if [ "$ENVIRONMENT" == "local" ]; then
        echo "Testing local endpoints..."
        
        # Test backend API (requires port-forward)
        echo "(Requires: kubectl port-forward svc/vendor-service 8080:8080)"
        echo "(Requires: kubectl port-forward svc/vendor-dashboard 3000:80)"
    else
        echo "Testing external endpoints (may require DNS)..."
        
        # Only test if DNS is configured
        test_warn "Skipping external endpoint tests (configure DNS first)"
        test_warn "Configure these DNS records:"
        echo "  - vendor.marketplace.com → Ingress IP"
        echo "  - api.vendor.marketplace.com → Ingress IP"
    fi
    
    # ==========================================
    # 14. SUMMARY
    # ==========================================
    print_header "Verification Summary"
    
    echo -e "${GREEN}Passed: $PASSED${NC}"
    echo -e "${RED}Failed: $FAILED${NC}"
    echo -e "${YELLOW}Warnings: $WARNINGS${NC}"
    
    echo ""
    if [ $FAILED -eq 0 ]; then
        echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║  Verification PASSED - Ready for use!  ║${NC}"
        echo -e "${GREEN}╚════════════════════════════════════════╝${NC}"
        return 0
    else
        echo -e "${RED}╔════════════════════════════════════════╗${NC}"
        echo -e "${RED}║  Verification FAILED - See errors above ║${NC}"
        echo -e "${RED}╚════════════════════════════════════════╝${NC}"
        return 1
    fi
}

# Run verification
main
