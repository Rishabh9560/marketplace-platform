# CI/CD Pipeline Documentation - Phase 4

## 📋 Overview

This document describes the complete GitHub Actions CI/CD pipeline for the Marketplace Platform. The pipeline automates:

- **Build & Testing**: Compile code, run tests, code quality checks
- **Docker Image Building**: Multi-architecture container images with security scanning
- **Deployment**: Automated staging and production deployments
- **Monitoring**: Post-deployment verification and health checks
- **Security**: Vulnerability scanning, secret detection, SAST analysis

## 🔄 Workflow Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        GitHub Repository                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Push to feature/* → PR opened                                 │
│       │                    │                                    │
│       ▼                    ▼                                    │
│  ┌──────────┐   ┌──────────────────┐                           │
│  │ Build &  │   │PR Validation     │                           │
│  │ Test     │   │- Title format    │                           │
│  └──────────┘   │- Branch naming   │                           │
│       │         │- Changed files   │                           │
│       ▼         └──────────────────┘                           │
│  ┌──────────┐                                                  │
│  │Security  │                                                  │
│  │Scanning  │                                                  │
│  └──────────┘                                                  │
│       │                                                         │
│  Approved & Merged to main                                     │
│       │                                                         │
│       ▼                                                         │
│  ┌─────────────────────────────────────────┐                  │
│  │ Docker Build & Push                     │                  │
│  │ - Build backend image                   │                  │
│  │ - Build frontend image                  │                  │
│  │ - Scan for vulnerabilities              │                  │
│  │ - Push to registry (GHCR)               │                  │
│  └─────────────────────────────────────────┘                  │
│       │                                                         │
│       ▼                                                         │
│  ┌─────────────────────────────────────────┐                  │
│  │ Deploy to Staging                       │                  │
│  │ - Update image references               │                  │
│  │ - Rolling update deployment             │                  │
│  │ - Verify services are healthy           │                  │
│  │ - Run smoke tests                       │                  │
│  │ - Notify team                           │                  │
│  └─────────────────────────────────────────┘                  │
│       │                                                         │
│  Manual approval for production                               │
│       │                                                         │
│       ▼                                                         │
│  ┌─────────────────────────────────────────┐                  │
│  │ Deploy to Production                    │                  │
│  │ - Pre-deployment checks                 │                  │
│  │ - Database backup                       │                  │
│  │ - Blue-green deployment                 │                  │
│  │ - Health check verification             │                  │
│  │ - Integration tests                     │                  │
│  │ - Post-deployment monitoring            │                  │
│  │ - Team notification                     │                  │
│  └─────────────────────────────────────────┘                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 📁 Workflow Files

### 1. `backend-build.yml`
**Trigger**: Push/PR to main/develop with backend/* changes

**Steps**:
1. Checkout code
2. Setup Java 21
3. Compile with Maven
4. Run unit tests
5. Run integration tests
6. Generate test reports
7. SpotBugs code quality check
8. Docker image scanning (Trivy)
9. SonarQube analysis (optional)
10. Dependency vulnerability check (OWASP)

**Artifacts**:
- Test results (`target/surefire-reports/`)
- Coverage reports (`target/site/`)

### 2. `frontend-build.yml`
**Trigger**: Push/PR to main/develop with vendor-dashboard/* changes

**Steps**:
1. Checkout code
2. Setup Node.js 20
3. Install dependencies
4. ESLint checking
5. TypeScript type checking
6. Run tests
7. Build production bundle
8. Bundle size analysis (max 20MB)
9. Security scanning:
   - npm audit
   - OWASP Dependency Check
   - Trivy Docker image scan

**Artifacts**:
- Distribution build (`vendor-dashboard/dist/`)
- Coverage reports

### 3. `docker-build-push.yml`
**Trigger**: Push to main branch (automated)

**Jobs**:
- **build-backend**: Multi-architecture Docker build, push to GHCR and Docker Hub
  - Platforms: linux/amd64, linux/arm64
  - SBOM generation
  - Vulnerability scanning

- **build-frontend**: Node.js build + Docker image
  - Production bundle
  - Vulnerability scanning

- **verify-images**: Image verification and size checks

**Registry Destinations**:
- GitHub Container Registry (GHCR): `ghcr.io/your-org/marketplace-*`
- Docker Hub: `docker.io/your-username/marketplace-*`

### 4. `deploy-staging.yml`
**Trigger**: Push to main (automated) or manual workflow_dispatch

**Environment**: `marketplace-staging`

**Steps**:
1. Pre-deployment checks
2. Configure kubectl for staging cluster
3. Apply namespace and configuration
4. Create image pull secrets
5. Update image references to latest SHA
6. Deploy storage, database, backend, frontend
7. Health checks and verification
8. Slack notification

**URL**: https://vendor-staging.marketplace.com

### 5. `deploy-production.yml`
**Trigger**: Manual workflow_dispatch with version and approver

**Environment**: `marketplace` (production)

**Pre-deployment**:
- Verify version format (semantic: v1.0.0)
- Check CHANGELOG
- Pre-deployment approval

**Deployment**:
1. Database backup
2. Blue-green deployment strategy
3. Backend service update (gradual rollout)
4. Frontend service update
5. Networking layer
6. Auto-scaling configuration
7. Monitoring setup
8. Integration tests
9. Deployment report generation
10. Team notification via Slack

**Post-deployment**:
- Health checks
- Deployment report
- Monitoring dashboard links

### 6. `security-scan.yml`
**Trigger**: Push, schedule (weekly), manual

**Security Scans**:
1. **OWASP Dependency Check**: Backend + Frontend dependencies
2. **Trivy Container Scanning**: Image vulnerability scan
3. **CodeQL**: Static analysis (Java + JavaScript)
4. **TruffleHog**: Secret detection in git history
5. **GitGuardian**: Credential leaks (optional)
6. **License Check**: Verify licensed dependencies

**Results**: SARIF reports uploaded to GitHub Security tab

### 7. `pr-validation.yml`
**Trigger**: Pull request to main/develop

**Validations**:
1. PR title format (conventional commits)
2. PR description present
3. Branch naming convention
4. Detect changed files
5. Backend checks (if backend/* changed):
   - Code formatting
   - Tests
6. Frontend checks (if vendor-dashboard/* changed):
   - Linting
   - Type checking
   - Build verification
   - Bundle size
7. Kubernetes validation (if k8s/* changed):
   - kubeval manifest validation
   - Security checks (non-root, no privileged)
8. Documentation review
9. Final checklist comment

## 🔐 GitHub Secrets Required

### Registry Access
```
REGISTRY_USERNAME      # GitHub username or service account
REGISTRY_PASSWORD      # GitHub token with packages scope
DOCKERHUB_USERNAME     # Docker Hub username (optional)
DOCKERHUB_TOKEN        # Docker Hub token (optional)
```

### Kubernetes Access
```
KUBE_CONFIG_STAGING    # Base64-encoded kubeconfig for staging
KUBE_CONFIG_PRODUCTION # Base64-encoded kubeconfig for production
```

### Notifications
```
SLACK_WEBHOOK_URL      # Slack incoming webhook for notifications
SLACK_CHANNEL          # Slack channel (optional)
```

### Code Quality (Optional)
```
SONAR_TOKEN            # SonarQube authentication token
GITGUARDIAN_API_KEY    # GitGuardian API key for secret scanning
```

## 🚀 Typical Workflow

### Development → Staging → Production

```
1. Developer creates feature branch
   ✓ Tests on local machine
   ✓ Pushes code

2. GitHub Actions Triggers
   ✓ Build & Test workflow runs
   ✓ Security scanning runs
   ✓ PR validation checks

3. Code Review
   ✓ Team reviews PR
   ✓ Approves changes

4. Merge to Main
   ✓ PR merged to main
   ✓ Docker build & push starts
   ✓ Staging deployment starts

5. Staging Verification
   ✓ Smoke tests run
   ✓ Team verifies functionality
   ✓ Performance baseline

6. Production Release (Manual)
   ✓ Maintainer runs production deployment
   ✓ Enters version (v1.2.3)
   ✓ Deployment proceeds:
     - Database backup
     - Blue-green deployment
     - Health checks
     - Notifications

7. Post-Deployment
   ✓ Monitor metrics
   ✓ Watch logs
   ✓ Track incidents
```

## 📊 Monitoring & Observability

### Real-time Dashboards
- **GitHub Actions**: https://github.com/your-org/marketplace-platform/actions
- **GHCR Packages**: https://github.com/your-org/packages
- **GitHub Security**: https://github.com/your-org/marketplace-platform/security

### Post-Deployment Monitoring
- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Dashboard visualization
- **Alert Manager**: Critical alerts
- **Logs**: kubectl logs for troubleshooting

### Slack Notifications
- Workflow status (success/failure)
- Deployment announcements
- Security alerts
- Performance anomalies

## 🔄 Conditional Triggers

### Backend Changes Only
```yaml
paths:
  - 'backend/**'
  - '.github/workflows/backend-build.yml'
```
Triggers: backend-build.yml only

### Frontend Changes Only
```yaml
paths:
  - 'vendor-dashboard/**'
  - '.github/workflows/frontend-build.yml'
```
Triggers: frontend-build.yml only

### Kubernetes Changes Only
```yaml
paths:
  - 'k8s/**'
  - '.github/workflows/*'
```
Triggers: pr-validation.yml

## 🛡️ Security Considerations

1. **Image Scanning**: All images scanned with Trivy before push
2. **Dependency Checks**: OWASP checks for vulnerable deps
3. **Secret Detection**: TruffleHog scans for exposed secrets
4. **SAST**: CodeQL performs static analysis
5. **Secrets Management**: All secrets via GitHub Secrets (encrypted)
6. **Approval Workflow**: Production deployments require manual approval
7. **Audit Trail**: All deployments logged with actor, time, version
8. **Network Policies**: Kubernetes NetworkPolicies restrict traffic
9. **RBAC**: Service accounts with minimal permissions
10. **Pod Security**: Non-root, read-only filesystems

## 📈 Performance Optimization

### Caching Strategies
- **Maven**: Caches dependencies in ~/.m2
- **npm**: Caches node_modules in ~/.npm
- **Docker**: Layer caching for multi-stage builds
- **Docker Buildx**: GHA cache backend

### Parallel Execution
- Backend & frontend builds run in parallel
- Multiple test suites run concurrently
- Security scans execute in parallel

### Build Times
- Backend build: ~5-7 minutes (first), ~2-3 minutes (cached)
- Frontend build: ~3-5 minutes (first), ~1-2 minutes (cached)
- Docker push: ~2-3 minutes
- Staging deployment: ~10-15 minutes
- Production deployment: ~15-20 minutes

## 🔧 Customization Guide

### Adding New Workflows
1. Create file in `.github/workflows/`
2. Define trigger events (on:)
3. Implement job steps
4. Add to README documentation

### Modifying Build Steps
1. Edit workflow YAML
2. Commit to feature branch
3. PR will test workflow logic
4. Merge to activate

### Updating Deployment Configs
1. Edit k8s/ manifests
2. Changes tested in staging
3. Production deployment verified

### Scaling Considerations
- Add more nodes before increasing replicas
- Monitor resource usage with kubectl top
- Adjust resource limits based on metrics
- Use HPA for automatic scaling

## 🐛 Troubleshooting

### Build Failures
```bash
# View workflow logs
gh run view <run-id> --log

# View specific job
gh run view <run-id> --log --job <job-id>
```

### Deployment Issues
```bash
# Check deployment status
kubectl rollout status deployment/vendor-service -n marketplace

# View pod logs
kubectl logs -f deployment/vendor-service -n marketplace

# Describe pod
kubectl describe pod <pod-name> -n marketplace
```

### Registry Issues
```bash
# Check image exists
docker pull ghcr.io/your-org/marketplace-vendor-service:latest

# Check image details
docker inspect ghcr.io/your-org/marketplace-vendor-service:latest
```

## 📞 Support

For CI/CD issues:
1. Check GitHub Actions logs
2. Review workflow YAML
3. Verify secrets configuration
4. Consult GitHub Actions documentation

## 🔜 Future Enhancements

- [ ] Automated rollback on failed health checks
- [ ] Canary deployments with traffic splitting
- [ ] Performance regression testing
- [ ] Automated rollback on SLO violations
- [ ] Multi-region deployments
- [ ] Disaster recovery automation
- [ ] Cost optimization alerts

---

**Version**: 1.0  
**Last Updated**: 2024  
**Status**: Production Ready ✅
