# Kubernetes Deployment Guide for Marketplace Platform

Complete Kubernetes manifests and documentation for deploying the vendor-service microservice, vendor-dashboard frontend, and supporting infrastructure.

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Directory Structure](#directory-structure)
4. [Deployment Instructions](#deployment-instructions)
5. [Frontend Deployment](#frontend-deployment)
6. [Configuration Management](#configuration-management)
7. [Monitoring & Observability](#monitoring--observability)
8. [Scaling & Performance](#scaling--performance)
9. [Security](#security)
10. [Troubleshooting](#troubleshooting)
11. [Production Checklist](#production-checklist)

## Architecture Overview

### Full Stack Components

```
┌──────────────────────────────────────────────────────────────────────┐
│                       Kubernetes Cluster                             │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │                 Ingress Controller (Nginx)                    │  │
│  │  - api.vendor.marketplace.com → vendor-service:8080          │  │
│  │  - vendor.marketplace.com → vendor-dashboard:80              │  │
│  │  - TLS/SSL, Rate Limiting, CORS                              │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                            ↓                                          │
│  ┌─────────────────────────────────┬──────────────────────────────┐  │
│  │                                 │                              │  │
│  │  Backend Service Layer          │   Frontend Layer            │  │
│  │  ─────────────────────────       │   ─────────────            │  │
│  │  ┌──────────────────────────┐    │   ┌────────────────────┐   │  │
│  │  │ Deployment: vendor-service   │   │   Deployment:      │   │  │
│  │  │ (3 replicas)                 │   │   vendor-dashboard │   │  │
│  │  │ - Spring Boot 3.x            │   │   (2 replicas)     │   │  │
│  │  │ - Health Probes              │   │   - React 19       │   │  │
│  │  │ - Security Context           │   │   - UI Components  │   │  │
│  │  │ - RBAC                       │   │   - Responsive     │   │  │
│  │  │ - Resource Limits            │   │   - SPA Routing    │   │  │
│  │  │ - Pod Anti-affinity          │   │   - API Calls      │   │  │
│  │  │ - Graceful Shutdown          │   │   - Caching        │   │  │
│  │  └──────────────────────────┘    │   └────────────────────┘   │  │
│  │           ↓                       │            ↓               │  │
│  │  ┌──────────────────────────┐    │   ┌────────────────────┐   │  │
│  │  │ Service: vendor-service  │    │   │ Service:           │   │  │
│  │  │ ClusterIP:8080           │    │   │ vendor-dashboard   │   │  │
│  │  │ Load Balanced            │    │   │ ClusterIP:80       │   │  │
│  │  └──────────────────────────┘    │   └────────────────────┘   │  │
│  └─────────────────────────────────┴──────────────────────────────┘  │
│           ↓                       │            ↑                     │
│  ┌────────────────────┐   ┌──────────────┐   Browser HTTP/HTTPS    │
│  │  StatefulSet:      │   │  Service:    │                          │
│  │  vendor-db         │   │  redis       │                          │
│  │  PostgreSQL 15     │   │  Cache       │                          │
│  │  - 1 replica       │   │  - 6379      │                          │
│  │  - HA ready        │   └──────────────┘                          │
│  │  - Monitoring      │                                             │
│  └────────────────────┘                                             │
│           ↓                                                          │
│  ┌────────────────────┐                                             │
│  │ Storage            │                                             │
│  │ - 50GB DB vol      │                                             │
│  │ - 100GB Backup     │                                             │
│  └────────────────────┘                                             │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │        Observability Stack                                  │  │
│  │  - Prometheus: Metrics collection (:9090)                   │  │
│  │  - Grafana: Dashboards (:3000)                              │  │
│  │  - Alert Manager: Alerting                                  │  │
│  │  - Jaeger: Distributed tracing                              │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

## Prerequisites

### System Requirements

- **Kubernetes**: 1.24+ (tested with 1.28)
- **Container Runtime**: Docker 20.10+ or containerd 1.6+
- **Storage**: 
  - 50GB for database (SSD recommended)
  - 100GB for backups (standard storage)
- **Compute**:
  - Minimum: 3 nodes (2 CPUs, 4GB RAM each)
  - Recommended: 5+ nodes (4 CPUs, 8GB RAM each)
  - Per-node storage: 200GB available

### Tools Required

```bash
# Kubernetes CLI
kubectl version --client

# Package manager for Kubernetes
helm version

# Container registry (for image storage)
# Docker Hub, ECR, GCR, or private registry

# SSL/TLS Certificates
# Let's Encrypt (automated via cert-manager)
```

### Cluster Setup Verification

```bash
# Check cluster connectivity
kubectl cluster-info

# Verify node status
kubectl get nodes -o wide

# Check available resources
kubectl describe nodes | grep -A 5 "Allocated resources"

# Verify storage classes
kubectl get storageclass

# Ensure ingress controller is available
kubectl get pods -n ingress-nginx
```

## Directory Structure

```
k8s/
├── 00-namespace.yaml           # Namespace creation, quotas, policies
├── 01-configmap.yaml           # Application configuration
├── 02-secrets.yaml             # Sensitive data (CHANGE FOR PRODUCTION)
├── 03-storage.yaml             # PersistentVolumes and PersistentVolumeClaims
├── 04-postgres.yaml            # PostgreSQL StatefulSet
├── 05-deployment.yaml          # Vendor Service Deployment
├── 06-service.yaml             # Service discovery
├── 07-ingress.yaml             # Original ingress (deprecated, use 13-marketplace-ingress.yaml)
├── 08-autoscaling.yaml         # Auto-scaling setup
├── 09-monitoring.yaml          # Prometheus monitoring
├── 10-dashboard-deployment.yaml # Frontend Deployment
├── 11-dashboard-service.yaml   # Frontend Service & NetworkPolicy
├── 12-dashboard-config.yaml    # Frontend ConfigMap & HPA
├── 13-marketplace-ingress.yaml # Combined Ingress (Backend + Frontend)
├── README.md                    # This file
└── docker-compose.yml          # Local development stack
```

## Deployment Instructions

### Step 1: Create Namespace and Apply Base Configuration

```bash
# Apply namespace with resource quotas and network policies
kubectl apply -f 00-namespace.yaml

# Verify namespace creation
kubectl get namespace marketplace
kubectl describe namespace marketplace
```

### Step 2: Configure Secrets

⚠️  **CRITICAL**: Update the following in `02-secrets.yaml` BEFORE deployment:

```bash
# Generate secure JWT secret
openssl rand -base64 32

# Generate secure database password
openssl rand -base64 16

# Generate secure Redis password
openssl rand -base64 20

# Update 02-secrets.yaml with these values
# Then apply:
kubectl apply -f 02-secrets.yaml

# Verify secrets (names only shown)
kubectl get secrets -n marketplace
```

### Step 3: Setup Storage

```bash
# Create storage directories on node (if using hostPath)
ssh user@node1 "mkdir -p /mnt/data/vendor-db /mnt/backup/vendor-db"
ssh user@node2 "mkdir -p /mnt/data/vendor-db /mnt/backup/vendor-db"
ssh user@node3 "mkdir -p /mnt/data/vendor-db /mnt/backup/vendor-db"

# Set permissions
ssh user@node1 "sudo chown 999:999 /mnt/data/vendor-db /mnt/backup/vendor-db && chmod 700 /mnt/data/vendor-db /mnt/backup/vendor-db"
ssh user@node2 "sudo chown 999:999 /mnt/data/vendor-db /mnt/backup/vendor-db && chmod 700 /mnt/data/vendor-db /mnt/backup/vendor-db"
ssh user@node3 "sudo chown 999:999 /mnt/data/vendor-db /mnt/backup/vendor-db && chmod 700 /mnt/data/vendor-db /mnt/backup/vendor-db"

# Apply storage configuration
kubectl apply -f 03-storage.yaml

# Verify PersistentVolumes
kubectl get pv
kubectl get pvc -n marketplace
```

### Step 4: Deploy Application Configuration

```bash
# Create ConfigMaps
kubectl apply -f 01-configmap.yaml

# For frontend (optional, if using separate image registry)
kubectl apply -f 12-dashboard-config.yaml

# Verify ConfigMaps
kubectl get configmap -n marketplace
```

### Step 5: Deploy PostgreSQL Database

```bash
# Deploy PostgreSQL StatefulSet
kubectl apply -f 04-postgres.yaml

# Wait for pod to be ready
kubectl wait --for=condition=ready pod -l app=vendor-db -n marketplace --timeout=300s

# Verify database is running
kubectl get statefulset -n marketplace
kubectl get pods -n marketplace -l app=vendor-db

# Connect to database to verify
kubectl exec -it vendor-db-0 -n marketplace -- psql -U postgres -c "SELECT version();"
```

### Step 6: Deploy Backend Service

```bash
# Deploy vendor-service
kubectl apply -f 05-deployment.yaml

# Wait for deployment to be ready
kubectl wait --for=condition=available --timeout=300s deployment/vendor-service -n marketplace

# Check pod status
kubectl get pods -n marketplace -l app=vendor-service
kubectl describe pod <pod-name> -n marketplace

# Check logs
kubectl logs -f deployment/vendor-service -n marketplace
```

### Step 7: Deploy Frontend Dashboard

```bash
# Build and push Docker image
docker build -t marketplace-vendor-dashboard:1.0.0 vendor-dashboard/
docker tag marketplace-vendor-dashboard:1.0.0 <registry>/marketplace-vendor-dashboard:1.0.0
docker push <registry>/marketplace-vendor-dashboard:1.0.0

# Update image in 10-dashboard-deployment.yaml if using private registry

# Deploy frontend
kubectl apply -f 10-dashboard-deployment.yaml
kubectl apply -f 11-dashboard-service.yaml
kubectl apply -f 12-dashboard-config.yaml

# Wait for ready
kubectl wait --for=condition=available --timeout=300s deployment/vendor-dashboard -n marketplace

# Verify frontend pods
kubectl get pods -n marketplace -l app=vendor-dashboard
```

### Step 8: Setup Services and Load Balancing

```bash
# Deploy services
kubectl apply -f 06-service.yaml
kubectl apply -f 11-dashboard-service.yaml

# Verify services
kubectl get svc -n marketplace
kubectl get endpoints -n marketplace
```

### Step 9: Configure Ingress and TLS

```bash
# Install cert-manager (if not already installed)
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
kubectl wait --for=condition=ready pod -l app.kubernetes.io/instance=cert-manager -n cert-manager --timeout=300s

# Install nginx ingress controller (if not already installed)
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install nginx-ingress ingress-nginx/ingress-nginx \
  --namespace ingress-nginx --create-namespace \
  --set controller.service.type=LoadBalancer

# Deploy combined ingress
kubectl apply -f 13-marketplace-ingress.yaml

# Wait for certificate issuance
kubectl wait --for=condition=ready certificate/marketplace-cert -n marketplace --timeout=600s

# Verify ingress
kubectl get ingress -n marketplace
kubectl describe ingress marketplace-ingress -n marketplace
kubectl get certificate -n marketplace
```

### Step 10: Setup Auto-scaling and Monitoring

```bash
# Deploy auto-scaling rules
kubectl apply -f 08-autoscaling.yaml

# Verify HPA
kubectl get hpa -n marketplace
kubectl describe hpa vendor-service -n marketplace

# Deploy monitoring (requires Prometheus operator)
kubectl apply -f 09-monitoring.yaml

# Verify monitoring setup
kubectl get servicemonitor -n marketplace
kubectl get prometheusrules -n marketplace
```

## Frontend Deployment

### Understanding the Frontend Architecture

The vendor-dashboard is a React 19 SPA (Single Page Application) deployed as a containerized service:

**Technology Stack:**
- **Framework**: React 19 with TypeScript
- **Build Tool**: Vite 5 (optimized for production)
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **HTTP Client**: Axios with interceptors
- **Container**: Node.js on Alpine Linux
- **Web Server**: Serve (npm package for static file serving)

**Docker Image Build Process:**
1. **Builder Stage**: 
   - Node.js 20 Alpine
   - Install dependencies (`npm ci`)
   - Build application (`npm run build`)
   - Output: optimized dist/ directory

2. **Runtime Stage**:
   - Node.js 20 Alpine (smaller image)
   - Copy only dist/ from builder
   - Serve static files on port 3000
   - Non-root user for security

**Expected Image Size**: ~150-200MB

### Building the Frontend Docker Image

```bash
# Build image
cd vendor-dashboard
docker build -t marketplace-vendor-dashboard:1.0.0 .

# Tag for registry
docker tag marketplace-vendor-dashboard:1.0.0 <your-registry>/marketplace-vendor-dashboard:1.0.0

# Push to registry
docker push <your-registry>/marketplace-vendor-dashboard:1.0.0

# Test locally
docker run -p 3000:3000 marketplace-vendor-dashboard:1.0.0
# Access at http://localhost:3000
```

### Configuration for Frontend

**Environment Variables** (set in 12-dashboard-config.yaml):

```yaml
API_URL: "http://vendor-service.marketplace.svc.cluster.local:8080/api/v1"  # Internal K8s DNS
VITE_API_URL: "https://api.vendor.marketplace.com/api/v1"                     # Browser requests
NODE_ENV: "production"
LOG_LEVEL: "info"
CACHE_TIMEOUT: "3600"
```

**Important**: The frontend uses two API URLs:
1. **Internal** (`API_URL`): Used by server-side rendering or health checks
2. **Browser** (`VITE_API_URL`): Used by frontend code running in browser

### Frontend Deployment Details

**Deployment Configuration** (10-dashboard-deployment.yaml):

- **Replicas**: 2 (for high availability)
- **Graceful Shutdown**: 30 seconds termination grace period
- **Health Checks**:
  - Liveness: HTTP GET / (30s delay, 10s period)
  - Readiness: HTTP GET / (10s delay, 5s period)
  - Startup: HTTP GET / (0s delay, 5s period, 30 failures = 150s timeout)
- **Resources**:
  - Requests: 100m CPU, 128Mi RAM
  - Limits: 500m CPU, 512Mi RAM
- **Security**:
  - Non-root user (UID 1000)
  - Read-only filesystem
  - Drop all Linux capabilities
  - No privilege escalation
- **Volumes**:
  - `/tmp` (emptyDir 512Mi): Temp files
  - `/app/.cache` (emptyDir 256Mi): Build cache
  - `/app/node_modules/.cache` (emptyDir 256Mi): Node cache

**Service Configuration** (11-dashboard-service.yaml):

- **Type**: ClusterIP (internal only)
- **Port**: 80 (HTTP)
- **SessionAffinity**: ClientIP (3600s) - sticky sessions
- **Network Policy**: Allows traffic from nginx-ingress controller only

**Auto-scaling** (12-dashboard-config.yaml):

- **Min Replicas**: 2
- **Max Replicas**: 5
- **CPU Target**: 70% utilization
- **Memory Target**: 80% utilization
- **Scale-up**: 100% per 30 seconds (aggressive)
- **Scale-down**: 50% per 60 seconds (conservative)

### Accessing the Frontend

After deployment, access the frontend at:

- **Development**: `http://localhost:3000`
- **Staging**: `https://vendor.marketplace.com`
- **Production**: `https://dashboard.vendor.marketplace.com`

**Note**: HTTPS requires proper DNS configuration and Let's Encrypt certificates.

## Configuration Management

### Updating Application Configuration

```bash
# Edit ConfigMap
kubectl edit configmap dashboard-config -n marketplace

# OR apply updated file
kubectl apply -f 12-dashboard-config.yaml

# Restart pods to pick up changes
kubectl rollout restart deployment/vendor-dashboard -n marketplace

# Verify rollout
kubectl rollout status deployment/vendor-dashboard -n marketplace
```

### Updating Secrets

```bash
# Update secret
kubectl patch secret marketplace-secrets -n marketplace \
  -p '{"data":{"jwt-secret":"'$(echo -n 'new-secret' | base64)'"}}'

# Restart pods
kubectl rollout restart deployment/vendor-service -n marketplace
kubectl rollout restart deployment/vendor-dashboard -n marketplace
```

### Database Connection String

Default connection (from ConfigMap):
```
jdbc:postgresql://vendor-db.marketplace.svc.cluster.local:5432/vendor_db
```

To use different database:
1. Update `01-configmap.yaml` with new connection details
2. Apply: `kubectl apply -f 01-configmap.yaml`
3. Restart service: `kubectl rollout restart deployment/vendor-service -n marketplace`

## Monitoring & Observability

### Accessing Logs

```bash
# Backend service logs
kubectl logs -f deployment/vendor-service -n marketplace --tail=100

# Frontend logs
kubectl logs -f deployment/vendor-dashboard -n marketplace --tail=100

# Database logs
kubectl logs -f statefulset/vendor-db -n marketplace --tail=100

# View logs for specific pod
kubectl logs -f <pod-name> -n marketplace

# Follow logs from all pods in deployment
kubectl logs -f deployment/vendor-service -n marketplace --all-containers=true
```

### Health Checks

```bash
# Check service health
kubectl exec -it deployment/vendor-service -n marketplace -- \
  curl http://localhost:8080/api/v1/actuator/health

# Check database connectivity
kubectl exec -it vendor-db-0 -n marketplace -- \
  psql -U postgres -c "SELECT 1"

# Check frontend health
kubectl exec -it deployment/vendor-dashboard -n marketplace -- \
  curl -f http://localhost:3000 || echo "Service unhealthy"
```

### Prometheus Metrics

```bash
# Access Prometheus
kubectl port-forward -n monitoring svc/prometheus 9090:9090

# Query metrics
# Visit http://localhost:9090

# Common queries:
# - CPU usage: container_cpu_usage_seconds_total
# - Memory usage: container_memory_usage_bytes
# - Request rate: http_request_total
# - Error rate: http_request_errors_total
```

### Grafana Dashboards

```bash
# Access Grafana
kubectl port-forward -n monitoring svc/grafana 3000:3000

# Default credentials: admin/admin
# Visit http://localhost:3000
```

## Scaling & Performance

### Manual Scaling

```bash
# Scale backend service
kubectl scale deployment vendor-service --replicas=5 -n marketplace

# Scale frontend
kubectl scale deployment vendor-dashboard --replicas=3 -n marketplace

# Verify scaling
kubectl get pods -n marketplace
```

### Checking HPA Status

```bash
# View HPA metrics
kubectl get hpa -n marketplace -o wide

# Watch HPA in real-time
kubectl get hpa vendor-service -n marketplace --watch

# Detailed HPA status
kubectl describe hpa vendor-service -n marketplace
```

### Load Testing

```bash
# Install Apache Bench
sudo apt-get install apache2-utils

# Run load test
ab -n 10000 -c 100 https://api.vendor.marketplace.com/api/v1/vendors

# View results from metrics
kubectl top nodes
kubectl top pods -n marketplace
```

## Security

### RBAC (Role-Based Access Control)

The deployment includes RBAC configurations:

```bash
# View RBAC rules
kubectl get role -n marketplace -o yaml

# Check permissions
kubectl auth can-i get secrets --as=system:serviceaccount:marketplace:vendor-service -n marketplace
```

### NetworkPolicies

```bash
# View network policies
kubectl get networkpolicy -n marketplace

# Verify policy
kubectl describe networkpolicy vendor-service-ingress-netpol -n marketplace
```

### Pod Security

All pods run with:
- Non-root user (UID 1000)
- Read-only filesystem
- Dropped capabilities
- No privilege escalation

```bash
# Verify pod security context
kubectl get pod <pod-name> -n marketplace -o jsonpath='{.spec.securityContext}'
```

### Secret Management

⚠️  **Never commit secrets to version control**

For production:
- Use **Sealed Secrets** or **HashiCorp Vault**
- Rotate secrets regularly
- Audit secret access

```bash
# Seal a secret (if using Sealed Secrets)
kubectl seal secrets marketplace-secrets -n marketplace
```

## Troubleshooting

### Common Issues

#### 1. Pod Pending
```bash
kubectl describe pod <pod-name> -n marketplace
# Check: Node selector, PVC status, resource availability
```

#### 2. CrashLoopBackOff
```bash
kubectl logs --previous <pod-name> -n marketplace
# Check: Application startup logs, database connectivity
```

#### 3. ImagePullBackOff
```bash
# Verify image exists in registry
docker pull <registry>/marketplace-vendor-service:1.0.0

# Check image pull secrets
kubectl get secret marketplace-registrycredential -n marketplace
```

#### 4. Database Connection Failed
```bash
# Check database pod
kubectl get pods -n marketplace -l app=vendor-db

# Verify connection string in ConfigMap
kubectl get configmap -n marketplace -o yaml | grep DATABASE

# Test connection from service pod
kubectl exec -it deployment/vendor-service -n marketplace -- \
  psql -h vendor-db.marketplace.svc.cluster.local -U postgres -c "SELECT 1"
```

#### 5. Ingress Not Routing
```bash
# Check ingress
kubectl get ingress -n marketplace
kubectl describe ingress marketplace-ingress -n marketplace

# Verify DNS
nslookup api.vendor.marketplace.com

# Check certificate status
kubectl get certificate -n marketplace
kubectl describe certificate marketplace-cert -n marketplace
```

#### 6. High Memory Usage
```bash
# Check memory usage
kubectl top pods -n marketplace

# Adjust resource limits in deployment
kubectl set resources deployment vendor-service -n marketplace --limits=memory=1.5Gi
```

#### 7. API Errors in Frontend
```bash
# Check frontend logs for API calls
kubectl logs -f deployment/vendor-dashboard -n marketplace

# Verify CORS configuration in ingress
kubectl get ingress -n marketplace -o yaml | grep -A 10 cors

# Test API endpoint directly
curl -H "Origin: https://vendor.marketplace.com" \
     -H "Access-Control-Request-Method: GET" \
     https://api.vendor.marketplace.com/api/v1/vendors
```

### Debug Commands

```bash
# Get detailed pod information
kubectl describe pod <pod-name> -n marketplace

# View pod events
kubectl get events -n marketplace --sort-by='.lastTimestamp'

# Get resource usage
kubectl top pods -n marketplace
kubectl top nodes

# Debug container
kubectl debug pod <pod-name> -n marketplace -it

# Execute command in running pod
kubectl exec -it <pod-name> -n marketplace -- /bin/bash
```

## Production Checklist

### Pre-Deployment

- [ ] Update all secrets in `02-secrets.yaml`
- [ ] Configure DNS records for ingress hostnames
- [ ] Setup SSL/TLS certificates with Let's Encrypt
- [ ] Verify storage is available on nodes (50GB+ database, 100GB+ backup)
- [ ] Ensure nodes have sufficient resources (minimum 3 nodes, 4+ CPUs each)
- [ ] Configure image registry and pull secrets
- [ ] Test all images in registry
- [ ] Plan backup and disaster recovery strategy
- [ ] Review security policies and enable Pod Security Policy
- [ ] Setup monitoring and alerting
- [ ] Document runbook for operations team

### Deployment

- [ ] Apply manifests in order (00-13)
- [ ] Verify all pods are running and ready
- [ ] Check database initialization completed
- [ ] Verify frontend and backend can communicate
- [ ] Test HTTPS/TLS connectivity
- [ ] Verify health checks pass
- [ ] Confirm monitoring metrics being collected
- [ ] Test horizontal pod autoscaling
- [ ] Verify network policies are enforced
- [ ] Test backup and restore procedures

### Post-Deployment

- [ ] Monitor application logs for errors
- [ ] Check CPU/memory usage patterns
- [ ] Verify metrics in Prometheus
- [ ] Test API endpoints with load
- [ ] Validate frontend functionality in browser
- [ ] Test user authentication flow
- [ ] Verify database replication (if HA configured)
- [ ] Test failover scenarios
- [ ] Document any customizations made
- [ ] Schedule regular backup verification
- [ ] Setup on-call rotation for monitoring
- [ ] Create operational runbooks

### Security Verification

- [ ] All communication is encrypted (HTTPS)
- [ ] Database password is strong and changed
- [ ] JWT secret is randomized and secure
- [ ] Network policies are blocking unauthorized traffic
- [ ] RBAC prevents unauthorized access
- [ ] Audit logging is enabled
- [ ] Secrets are rotated regularly
- [ ] Image scanning shows no critical vulnerabilities
- [ ] Backup files are encrypted and stored securely
- [ ] Disaster recovery plan is tested

### Performance Optimization

- [ ] Review resource limits based on actual usage
- [ ] Tune database connection pool settings
- [ ] Configure Redis caching properly
- [ ] Enable HTTP compression in ingress
- [ ] Optimize frontend bundle size
- [ ] Setup CDN for static assets (optional)
- [ ] Configure read replicas for database (optional)
- [ ] Implement query optimization for slow queries
- [ ] Test with expected production load
- [ ] Document baseline performance metrics

### Ongoing Maintenance

- [ ] Daily: Monitor application logs and metrics
- [ ] Weekly: Review performance trends
- [ ] Weekly: Verify backups are successful
- [ ] Monthly: Review security updates
- [ ] Monthly: Capacity planning analysis
- [ ] Quarterly: Disaster recovery drill
- [ ] Quarterly: Security audit
- [ ] Semi-annually: Update Kubernetes cluster
- [ ] Semi-annually: Update application dependencies
- [ ] Annually: Review and update disaster recovery plan

## Post-Deployment Verification

### 1. Service Health

```bash
# Check all pods are running
kubectl get pods -n marketplace

# Verify services are accessible
kubectl get svc -n marketplace

# Test backend API
curl https://api.vendor.marketplace.com/api/v1/actuator/health

# Access frontend
curl https://vendor.marketplace.com
```

### 2. Database Verification

```bash
# Connect to database
kubectl exec -it vendor-db-0 -n marketplace -- psql -U postgres
\l  # List databases
\dt # List tables
```

### 3. Monitoring Setup

```bash
# Verify Prometheus targets
kubectl port-forward -n monitoring svc/prometheus 9090:9090
# Open http://localhost:9090/targets

# Verify Grafana dashboards
kubectl port-forward -n monitoring svc/grafana 3000:3000
# Open http://localhost:3000
```

### 4. Load Test

```bash
# Install Apache Bench
sudo apt-get install apache2-utils

# Simple load test
ab -n 1000 -c 10 https://api.vendor.marketplace.com/api/v1/health
```

### 5. Application Test

```bash
# Test login endpoint
curl -X POST https://api.vendor.marketplace.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"vendor@example.com","password":"password"}'

# Visit frontend
# Open https://vendor.marketplace.com in browser
# Test login form
# Verify dashboard loads
```

## Additional Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [cert-manager Documentation](https://cert-manager.io/docs/)
- [Prometheus Operator](https://prometheus-operator.dev/)
- [Spring Boot on Kubernetes](https://spring.io/blog/2017/07/04/demystifying-the-spring-boot-actuator)
- [React Production Build Guide](https://react.dev/learn/start-a-new-react-project)

## Support

For issues or questions:
1. Check the Troubleshooting section
2. Review application logs: `kubectl logs -f <pod-name> -n marketplace`
3. Contact your DevOps/SRE team
4. Reference the project documentation at `/docs`
