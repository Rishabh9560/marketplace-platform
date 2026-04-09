# GitHub Actions Setup Guide

## 🚀 Quick Setup (5 minutes)

### Step 1: Create GitHub Secrets

Navigate to: **Settings → Secrets and variables → Actions**

#### Registry Secrets
```
REGISTRY_USERNAME = <github-username>
REGISTRY_PASSWORD = <github-token>
```

**Generate GitHub Token**:
1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Scopes needed:
   - repo (full control)
   - write:packages
   - read:packages
4. Copy token to REGISTRY_PASSWORD

#### Kubernetes Secrets

**Get kubeconfig (staging)**:
```bash
# For staging cluster
kubectl config view --raw | base64 -w 0

# Copy output to KUBE_CONFIG_STAGING secret
```

**Get kubeconfig (production)**:
```bash
# For production cluster
kubectl config view --raw | base64 -w 0

# Copy output to KUBE_CONFIG_PRODUCTION secret
```

#### Slack Integration (Optional)
```
SLACK_WEBHOOK_URL = https://hooks.slack.com/services/YOUR/WEBHOOK/URL
```

**Create Slack Webhook**:
1. Go to https://api.slack.com/apps
2. Create New App → From scratch
3. Name: "GitHub Marketplace"
4. Choose workspace
5. Enable Incoming Webhooks
6. Add New Webhook to Workspace
7. Select channel and authorize

### Step 2: Add Action Workflows

All workflows are in `.github/workflows/`:

```bash
# Check workflows exist
ls -la .github/workflows/

# Expected files:
# - backend-build.yml
# - frontend-build.yml
# - docker-build-push.yml
# - deploy-staging.yml
# - deploy-production.yml
# - security-scan.yml
# - pr-validation.yml
```

### Step 3: Enable Actions

1. Go to **Settings → Actions → General**
2. "Allow all actions and reusable workflows"
3. "Save"

### Step 4: Configure Branch Protection

1. Go to **Settings → Branches**
2. Add rule for `main`:
   - Require pull request reviews before merging
   - Require status checks to pass:
     - Backend Build & Test
     - Frontend Build & Test
     - PR Validation
   - Require branches to be up to date

## 🔧 Secrets Configuration Details

### For Docker Hub (Optional)

If pushing to Docker Hub:

```
DOCKERHUB_USERNAME = your-username
DOCKERHUB_TOKEN = your-docker-hub-token
```

**Create Docker Hub Token**:
1. https://hub.docker.com/settings/security
2. Create new token
3. Copy token to secret

### For SonarQube (Optional)

If using SonarQube for code quality:

```
SONAR_TOKEN = sqp_...
```

**Get SonarQube Token**:
1. https://sonarcloud.io/account/security
2. Generate token
3. Copy to secret

### For GitGuardian (Optional)

If using GitGuardian for secret scanning:

```
GITGUARDIAN_API_KEY = ...
```

## ✅ Verification

### Verify Workflows are Recognized

Go to **Actions** tab in GitHub:
- Should see all 7 workflows
- Each shows trigger conditions

### Test PR Workflow

1. Create feature branch: `feature/test-ci`
2. Make small change
3. Push to GitHub
4. Create Pull Request
5. Observe PR Validation workflow

### Test Backend Build

1. Modify `backend/pom.xml` slightly
2. Commit and push
3. Create PR
4. Backend Build & Test workflow starts

### Test Frontend Build

1. Modify `vendor-dashboard/package.json`
2. Commit and push
3. Create PR
4. Frontend Build & Test workflow starts

## 📊 Monitoring Workflows

### GitHub Actions Dashboard

View at: **Your Repo → Actions**

Shows:
- All workflow runs
- Status (success/failure)
- Logs for each job
- Artifacts

### Workflow Status Badges

Add to README.md:

```markdown
![Backend Build](https://github.com/your-org/marketplace-platform/workflows/Backend%20Build%20%26%20Test/badge.svg)
![Frontend Build](https://github.com/your-org/marketplace-platform/workflows/Frontend%20Build%20%26%20Test/badge.svg)
![Docker Build](https://github.com/your-org/marketplace-platform/workflows/Build%20%26%20Push%20Docker%20Images/badge.svg)
```

## 🚨 Troubleshooting Setup

### Workflow Not Triggering

**Issue**: Workflow file exists but doesn't trigger

**Solutions**:
1. Verify workflow YAML is in `.github/workflows/`
2. Check `on:` triggers match your situation
3. Branch name must match in triggers
4. File paths in triggers must match changed files

### Docker Push Fails

**Issue**: `authentication required` error

**Solutions**:
1. Verify REGISTRY_USERNAME and REGISTRY_PASSWORD
2. Ensure GitHub token has `write:packages` scope
3. For Docker Hub, use DOCKERHUB_USERNAME/TOKEN

### Kubernetes Deployment Fails

**Issue**: `Unable to connect to the server`

**Solutions**:
1. Verify kubeconfig secret is valid:
   ```bash
   echo $KUBE_CONFIG_STAGING | base64 -d > /tmp/kube.config
   kubectl --kubeconfig=/tmp/kube.config get nodes
   ```
2. Check kubectl access to namespace
3. Verify service account permissions

### Slack Notification Not Sent

**Issue**: Deployment completes but no Slack message

**Solutions**:
1. Verify SLACK_WEBHOOK_URL is correct
2. Check webhook is still active in Slack
3. Webhook URL includes "https://"
4. Continue-on-error may hide errors

## 🔐 Security Best Practices

1. **Never commit secrets** to repository
2. **Use branch protection** requiring PR reviews
3. **Require status checks** before merging
4. **Audit secret access** regularly
5. **Rotate tokens** periodically (quarterly)
6. **Use service accounts** for CI/CD
7. **Limit permissions** to minimum required
8. **Enable 2FA** on GitHub account
9. **Review workflow logs** for sensitive data
10. **Keep actions updated** for security patches

## 📈 Performance Tips

### Speed Up Builds

1. **Cache dependencies**:
   - Maven: automatic with setup-java
   - npm: configure package-lock.json

2. **Use latest runner**:
   - `ubuntu-latest` auto-updates
   - Gets faster processors

3. **Parallel jobs**:
   - Backend and frontend build in parallel
   - Saves time

4. **Skip unnecessary steps**:
   - Security scans run on schedule
   - Tests only on PR/push

### Reduce Build Time

- Backend: ~2-3 min (cached)
- Frontend: ~1-2 min (cached)
- Docker build: ~2 min
- Deployment: ~10-15 min

**Total time**: ~15-20 minutes from commit to production deployment

## 📝 Workflow Customization

### Modify Build Duration

**In workflow files**, adjust timeouts:
```yaml
timeout-minutes: 10  # Increase if needed
```

### Skip CI/CD For Certain Commits

Add to commit message:
```
[skip ci]
```

### Run Workflow Manually

1. **Actions** → Select workflow
2. **Run workflow** button
3. Select branch and inputs

## 🔄 Workflow Best Practices

1. **Use consistent naming**: `backend-`, `frontend-`, `deploy-`
2. **Include failure notifications**: Always notify on failure
3. **Archive logs**: Keep for compliance
4. **Version control workflows**: Treat like code
5. **Document triggers**: Clear conditions
6. **Use reusable workflows**: Reduce duplication
7. **Version actions**: Pin to specific versions
8. **Test locally first**: Before committing workflows
9. **Monitor execution time**: Optimize as needed
10. **Keep clean**: Remove old workflows

## 📞 Getting Help

- **GitHub Actions Docs**: https://docs.github.com/en/actions
- **Workflow Syntax**: https://docs.github.com/en/actions/reference/workflow-syntax-for-github-actions
- **Troubleshooting**: https://docs.github.com/en/actions/troubleshooting

## ✨ Next Steps

1. ✅ Create GitHub Secrets
2. ✅ Enable Actions
3. ✅ Add workflows (already in repo)
4. ✅ Configure branch protection
5. ✅ Test PR workflow
6. ✅ Monitor first deployment
7. ✅ Iterate and optimize

---

**Phase 4 Complete!** 🎉

You now have full CI/CD automation for:
- Build & Test
- Docker Image Building
- Kubernetes Deployment
- Security Scanning
- PR Validation
- Notifications

Your platform is production-ready! 🚀
