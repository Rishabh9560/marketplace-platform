# Kubernetes Deployment Guide for Marketplace Platform

Complete Kubernetes manifests and documentation for deploying the vendor-service microservice and supporting infrastructure.

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Directory Structure](#directory-structure)
4. [Deployment Instructions](#deployment-instructions)
5. [Configuration Management](#configuration-management)
6. [Monitoring & Observability](#monitoring--observability)
7. [Scaling & Performance](#scaling--performance)
8. [Security](#security)
9. [Troubleshooting](#troubleshooting)
10. [Production Checklist](#production-checklist)

## Architecture Overview

### Components

```
┌─────────────────────────────────────────────────────────────┐
│                       Kubernetes Cluster                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  Ingress Controller (Nginx)          │  │
│  │                  - TLS/SSL Termination               │  │
│  │                  - Rate Limiting                     │  │
│  │                  - URL Routing                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                            ↓                                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │          Load Balancer Service                       │  │
│  │          (Round-robin traffic distribution)         │  │
│  └──────────────────────────────────────────────────────┘  │
│                            ↓                                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │     Deployment: vendor-service (3 replicas)         │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │ Pod 1: vendor-service container               │  │  │
│  │  │        - Spring Boot Application              │  │  │
│  │  │        - Health Checks (liveness/readiness)   │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │ Pod 2: vendor-service container               │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │ Pod 3: vendor-service container               │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────┘  │
│            ↓                                   ↓            │
│  ┌──────────────────────────────┐  ┌──────────────────────┐ │
│  │   StatefulSet: vendor-db     │  │  Service: Redis      │ │
│  │   - PostgreSQL Instance      │  │  - Cache Layer       │ │
│  │   - PersistentVolume         │  │  - Session Store     │ │
│  │   - Postgres Exporter        │  │  - Message Queue     │ │
│  └──────────────────────────────┘  └──────────────────────┘ │
│            ↓                                                  │
│  ┌──────────────────────────────┐                            │
│  │   Persistent Storage         │                            │
│  │   - 50GB Database Volume     │                            │
│  │   - 100GB Backup Volume      │                            │
│  └──────────────────────────────┘                            │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │        Monitoring & Logging Stack                   │  │
│  │  - Prometheus (Metrics)                             │  │
│  │  - Grafana (Visualization)                          │  │
│  │  - ELK Stack (Logs)                                 │  │
│  │  - Jaeger (Tracing)                                 │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## Prerequisites

### System Requirements

- **Kubernetes**: 1.24+ (tested with 1.28)
- **Container Runtime**: Docker 20.10+ or containerd 1.6+
- **Storage**: 
  - 50GB for database
  - 100GB for backups
- **Compute**:
  - Minimum: 3 nodes (2 CPUs, 4GB RAM each)
  - Recommended: 5+ nodes (4 CPUs, 8GB RAM each)

### Tools Required

```bash
# Install kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/

# Install helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Install cert-manager (for Let's Encrypt certificates)
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Install nginx-ingress
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm install nginx-ingress ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace

# Install prometheus-operator (optional, for monitoring)
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install kube-prometheus prometheus-community/kube-prometheus-stack
```

## Directory Structure

```
k8s/
├── 00-namespace.yaml          # Namespace, ResourceQuota, NetworkPolicy
├── 01-configmap.yaml          # Application configuration
├── 02-secrets.yaml            # Sensitive data (JWT, DB passwords, etc.)
├── 03-storage.yaml            # PersistentVolume & PersistentVolumeClaim
├── 04-postgres.yaml           # PostgreSQL StatefulSet + Service
├── 05-deployment.yaml         # Vendor service Deployment + ServiceAccount + RBAC
├── 06-service.yaml            # ClusterIP Service + Headless Service
├── 07-ingress.yaml            # Ingress + TLS + NetworkPolicy
├── 08-autoscaling.yaml        # HPA + PodDisruptionBudget + ResourceQuota
├── 09-monitoring.yaml         # ServiceMonitor + PrometheusRule
└── README.md                  # This file
```

## Deployment Instructions

### Step 1: Create Namespace & Initial Resources

```bash
kubectl create namespace marketplace

# Create namespace with resource quotas
kubectl apply -f k8s/00-namespace.yaml
```

### Step 2: Create Secrets & Configuration

```bash
# Create secrets (update with your values in production)
kubectl apply -f k8s/02-secrets.yaml

# Verify secrets are created
kubectl get secrets -n marketplace
```

⚠️ **Security Note**: In production, use one of these approaches:
- [Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets)
- [External Secrets Operator](https://external-secrets.io/)
- [HashiCorp Vault](https://www.vaultproject.io/)
- Cloud provider secret management (AWS Secrets Manager, Azure Key Vault, etc.)

### Step 3: Create ConfigMaps

```bash
kubectl apply -f k8s/01-configmap.yaml

# Verify ConfigMaps
kubectl get configmaps -n marketplace
```

### Step 4: Setup Storage

```bash
kubectl apply -f k8s/03-storage.yaml

# Verify PersistentVolume & PersistentVolumeClaim
kubectl get pv,pvc -n marketplace
```

### Step 5: Deploy Database (PostgreSQL)

```bash
kubectl apply -f k8s/04-postgres.yaml

# Wait for StatefulSet to be ready
kubectl rollout status statefulset/vendor-db -n marketplace

# Verify database is running
kubectl get statefulsets,pods -n marketplace
```

### Step 6: Deploy Vendor Service

```bash
# First, build and push Docker image
docker build -t marketplace-vendor-service:1.0.0 vendor-service/
docker tag marketplace-vendor-service:1.0.0 your-registry/marketplace-vendor-service:1.0.0
docker push your-registry/marketplace-vendor-service:1.0.0

# Update image in deployment.yaml
sed -i 's|marketplace-vendor-service:1.0.0|your-registry/marketplace-vendor-service:1.0.0|g' k8s/05-deployment.yaml

# Deploy vendor service
kubectl apply -f k8s/05-deployment.yaml

# Wait for deployment to be ready
kubectl rollout status deployment/vendor-service -n marketplace

# Check pod status
kubectl get pods -n marketplace
```

### Step 7: Setup Services & Load Balancing

```bash
kubectl apply -f k8s/06-service.yaml

# Verify services
kubectl get svc -n marketplace
```

### Step 8: Configure Ingress & TLS

```bash
# Update domain names in ingress.yaml
sed -i 's|api.vendor.marketplace.com|your-domain.com|g' k8s/07-ingress.yaml

kubectl apply -f k8s/07-ingress.yaml

# Wait for TLS certificate
kubectl wait --for=condition=Ready certificate/vendor-service-cert -n marketplace --timeout=300s

# Get Ingress IP
kubectl get ingress -n marketplace
```

### Step 9: Setup Auto-scaling

```bash
kubectl apply -f k8s/08-autoscaling.yaml

# Verify HPA
kubectl get hpa -n marketplace
```

### Step 10: Setup Monitoring

```bash
kubectl apply -f k8s/09-monitoring.yaml

# Verify ServiceMonitor
kubectl get servicemonitor -n marketplace
```

## Configuration Management

### Updating Application Configuration

```bash
# Edit ConfigMap
kubectl edit configmap vendor-service-config -n marketplace

# Restart pods to apply changes
kubectl rollout restart deployment/vendor-service -n marketplace
```

### Updating Secrets

⚠️ **Important**: Never commit secrets to Git!

```bash
# Create new secret file locally
cat > secrets-prod.yaml <<EOF
apiVersion: v1
kind: Secret
metadata:
  name: vendor-service-secrets
  namespace: marketplace
type: Opaque
data:
  JWT_SECRET: $(echo -n 'your-secret' | base64)
  # ... other secrets
EOF

# Apply
kubectl apply -f secrets-prod.yaml

# Restart deployment
kubectl rollout restart deployment/vendor-service -n marketplace
```

### Creating New Environment

```bash
# Copy manifests
cp -r k8s k8s-staging

# Create new namespace
kubectl create namespace marketplace-staging

# Update manifests with staging values
sed -i 's|marketplace|marketplace-staging|g' k8s-staging/*.yaml

# Apply
kubectl apply -f k8s-staging/
```

## Monitoring & Observability

### Accessing Metrics

```bash
# Forward Prometheus
kubectl port-forward svc/prometheus -n monitoring 9090:9090

# Forward Grafana
kubectl port-forward svc/grafana -n monitoring 3000:3000
```

### Viewing Logs

```bash
# View logs from single pod
kubectl logs -f deployment/vendor-service -n marketplace

# View logs from all pods
kubectl logs -f deployment/vendor-service -n marketplace --all-containers=true

# View logs with timestamps and previous crashes
kubectl logs --previous -f deployment/vendor-service -n marketplace

# Stream logs from all pods in deployment
kubectl logs -f -l app=vendor-service -n marketplace --all-containers=true
```

### Health Checks

```bash
# Check pod health
kubectl describe pod vendor-service-xxx -n marketplace

# Check deployment status
kubectl describe deployment vendor-service -n marketplace

# Test service connectivity
kubectl run -it --rm debug --image=busybox --restart=Never -- sh
# Inside pod:
# wget -O- http://vendor-service.marketplace.svc.cluster.local:8080/api/v1/actuator/health
```

### Database Access

```bash
# Connect to PostgreSQL
kubectl exec -it vendor-db-0 -n marketplace -- psql -U marketplace -d vendor_service

# Inside psql:
# \dt - list tables
# SELECT * FROM vendors LIMIT 5;
# \q - quit
```

## Scaling & Performance

### Manual Scaling

```bash
# Scale deployment
kubectl scale deployment vendor-service --replicas=5 -n marketplace

# Check scaling status
kubectl get deployment vendor-service -n marketplace
```

### Monitoring HPA

```bash
# Check HPA status
kubectl get hpa -n marketplace

# Describe HPA
kubectl describe hpa vendor-service-hpa -n marketplace

# Watch HPA metrics
kubectl get hpa vendor-service-hpa -n marketplace -w
```

### Load Testing

```bash
# Install Apache Bench
sudo apt-get install apache2-utils

# Run load test
ab -n 10000 -c 100 http://api.vendor.marketplace.com/api/v1/vendors

# Using hey
go install github.com/rakyll/hey@latest
hey -n 10000 -c 100 http://api.vendor.marketplace.com/api/v1/vendors
```

## Security

### Network Policies

All ingress/egress traffic is controlled by NetworkPolicy. Current rules:
- Allow ingress from nginx-ingress controller only
- Allow egress to database on port 5432
- Allow egress to redis on port 6379
- Allow DNS resolution
- Block access to AWS metadata service (169.254.169.254)

### RBAC

```bash
# Check ServiceAccount permissions
kubectl get rolebindings -n marketplace

# Describe role
kubectl describe role vendor-service -n marketplace
```

### Pod Security

```bash
# Check security context
kubectl get pod vendor-service-xxx -o yaml | grep -A 10 securityContext
```

Implemented security measures:
- Non-root container (UID 1000)
- Read-only filesystem
- No privilege escalation
- Dropped ALL capabilities
- FSGroup set (2000)

### Secrets Management in Production

Update `02-secrets.yaml` with:
1. Strong JWT secret (generate: `openssl rand -hex 32`)
2. Strong database password
3. Real TLS certificates (not self-signed)
4. Actual image pull credentials

```bash
# Generate new JWT secret
openssl rand -hex 32 | base64

# Generate TLS certificate with Let's Encrypt (via cert-manager)
# Already configured in ingress.yaml
```

## Troubleshooting

### Deployment Not Ready

```bash
# Check deployment status
kubectl describe deployment vendor-service -n marketplace

# Check recent events
kubectl get events -n marketplace --sort-by='.lastTimestamp' | tail -20

# Check pod logs
kubectl logs -f deployment/vendor-service -n marketplace

# Check for ImagePullBackOff
kubectl describe pod vendor-service-xxx -n marketplace
```

### Database Connection Issues

```bash
# Check if database pod is running
kubectl get statefulsets -n marketplace

# Check database logs
kubectl logs vendor-db-0 -n marketplace

# Check connection from application pod
kubectl exec -it deployment/vendor-service -n marketplace -- bash
# Inside pod:
# nc -zv vendor-db.marketplace.svc.cluster.local 5432
```

### Ingress Not Working

```bash
# Check ingress status
kubectl describe ingress vendor-service-ingress -n marketplace

# Check ingress controller logs
kubectl logs -f -l app.kubernetes.io/name=ingress-nginx -n ingress-nginx

# Check certificate status
kubectl describe certificate vendor-service-cert -n marketplace
```

### Out of Memory (OOM)

```bash
# Check resource usage
kubectl top nodes
kubectl top pods -n marketplace

# Check OOM events
kubectl get events -n marketplace | grep OOMKilled

# Increase memory limits in deployment.yaml
# Restart deployment
kubectl rollout restart deployment/vendor-service -n marketplace
```

### Slow Queries

```bash
# Check slow query logs in PostgreSQL
kubectl exec -it vendor-db-0 -n marketplace -- psql -U marketplace -d vendor_service -c "SELECT query, mean_time, calls FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;"

# Check connection pool status
# Via application metrics endpoint
kubectl port-forward svc/vendor-service -n marketplace 8080:8080
# Visit http://localhost:8080/api/v1/actuator/metrics
```

## Production Checklist

Before deploying to production:

- [ ] Update all image registries and versions
- [ ] Change default passwords in secrets
- [ ] Configure real TLS certificates (Let's Encrypt or your CA)
- [ ] Set up external secret management (Sealed Secrets, Vault, etc.)
- [ ] Configure persistent backup strategy
- [ ] Set up monitoring and alerting (Prometheus, Grafana, PagerDuty)
- [ ] Configure log aggregation (ELK, Splunk, CloudWatch)
- [ ] Set up distributed tracing (Jaeger, Zipkin)
- [ ] Test disaster recovery procedures
- [ ] Configure network policies properly
- [ ] Set up resource requests/limits
- [ ] Configure HPA metrics and thresholds
- [ ] Test auto-scaling behavior
- [ ] Set up ingress with WAF (Web Application Firewall)
- [ ] Enable audit logging
- [ ] Configure pod security policies
- [ ] Set up RBAC properly
- [ ] Test failover scenarios
- [ ] Document runbooks for operations
- [ ] Set up on-call rotations
- [ ] Load test the deployment
- [ ] Set up DNS TTL strategy

## Post-Deployment Verification

```bash
# 1. Check all pods are running
kubectl get pods -n marketplace

# 2. Verify services are accessible
kubectl exec -it pod/debug -n marketplace -- curl http://vendor-service:8080/api/v1/actuator/health

# 3. Check ingress is working
curl -H "Host: api.vendor.marketplace.com" http://ingress-ip/api/v1/actuator/health

# 4. Verify database connection
kubectl exec -it pod/vendor-service-xxx -n marketplace -- curl http://localhost:8080/api/v1/vendors

# 5. Check metrics are being scraped
kubectl port-forward svc/prometheus -n monitoring 9090:9090
# Visit http://localhost:9090 and search for vendor_service metrics

# 6. Monitor pod status changes
kubectl get pods -n marketplace -w
```

## Additional Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot on Kubernetes](https://spring.io/blog/2021/01/04/spring-boot-on-kubernetes)
- [Kubernetes Security Best Practices](https://kubernetes.io/docs/concepts/security/pod-security-standards/)
- [NetworkPolicy Recipes](https://github.com/ahmetb/kubernetes-network-policy-recipes)

## Support & Updates

For issues or questions, refer to:
1. Check troubleshooting section above
2. Review pod logs and events
3. Consult Kubernetes documentation
4. Check application logs for errors
5. Review network policies and RBAC configuration

---

**Last Updated**: April 2026
**Kubernetes Version**: 1.28+
**Platform Version**: 1.0.0
