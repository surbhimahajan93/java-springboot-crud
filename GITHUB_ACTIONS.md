# GitHub Actions Workflows

This repository includes comprehensive GitHub Actions workflows for CI/CD, testing, code quality, and security.

## Workflows Overview

### 1. **CI/CD Pipeline** (`.github/workflows/ci.yml`)
**Triggers:** Push to `main`/`develop`, Pull Requests

**Jobs:**
- **Build and Test**: Runs unit and integration tests with MongoDB
- **Code Quality**: Runs SpotBugs and PMD static analysis
- **Security Scan**: Runs OWASP Dependency Check
- **Docker Build**: Builds Docker image (main branch only)
- **Notification**: Provides status feedback

### 2. **Pull Request Check** (`.github/workflows/pr-check.yml`)
**Triggers:** Pull Requests to `main`/`develop`

**Features:**
- Quick feedback on PRs
- Runs tests and builds
- Comments on PR with results
- Code formatting checks

### 3. **Deploy to Production** (`.github/workflows/deploy.yml`)
**Triggers:** Release publication, Manual dispatch

**Features:**
- Builds and pushes Docker image to GitHub Container Registry
- Deploys to staging/production environments
- Manual deployment options
- Environment-specific configurations

### 4. **Dependency Updates** (`.github/workflows/dependency-update.yml`)
**Triggers:** Weekly schedule (Mondays), Manual dispatch

**Features:**
- Automated dependency updates
- Security vulnerability scanning
- Creates PRs for updates
- Comments on PRs with security findings

## Workflow Features

### 🔧 **Build & Test**
- Java 17 with Temurin distribution
- Gradle caching for faster builds
- MongoDB service for integration tests
- Separate unit and integration test runs
- Test result artifacts

### 🛡️ **Code Quality**
- **SpotBugs**: Static analysis for potential bugs
- **PMD**: Code style and complexity analysis
- **Spotless**: Code formatting with Google Java Format
- **OWASP Dependency Check**: Security vulnerability scanning

### 🐳 **Docker Integration**
- Multi-platform Docker builds
- GitHub Container Registry integration
- Docker layer caching
- Image metadata and tagging

### 🔄 **Automation**
- Automated dependency updates
- PR creation for updates
- Security scanning integration
- Status notifications

## Configuration Files

### Code Quality Tools
- `config/spotbugs/exclude.xml` - SpotBugs exclusions
- `config/pmd/ruleset.xml` - PMD rules configuration
- `config/dependency-check/suppressions.xml` - OWASP suppressions

### Build Configuration
- `build.gradle` - Gradle plugins and configurations
- `application-test.properties` - Test environment settings

## Usage

### Local Development
```bash
# Run code quality checks locally
./gradlew spotbugsMain pmdMain spotlessCheck

# Run security scan locally
./gradlew dependencyCheckAnalyze

# Format code
./gradlew spotlessApply

# Check for dependency updates
./gradlew dependencyUpdates
```

### GitHub Actions
1. **Push to main/develop**: Triggers full CI/CD pipeline
2. **Create PR**: Triggers PR check workflow
3. **Publish release**: Triggers deployment workflow
4. **Weekly**: Automatic dependency updates

## Environment Variables

### Required Secrets
- `GITHUB_TOKEN` - Automatically provided by GitHub
- `DOCKER_REGISTRY_TOKEN` - For Docker registry access (if needed)

### Environment-Specific
- `SPRING_DATA_MONGODB_HOST` - MongoDB host for tests
- `SPRING_DATA_MONGODB_PORT` - MongoDB port for tests
- `SPRING_DATA_MONGODB_DATABASE` - MongoDB database for tests

## Artifacts

The workflows generate several artifacts:
- **Test Results**: HTML reports from JUnit tests
- **Code Quality Reports**: SpotBugs and PMD HTML reports
- **Security Reports**: OWASP Dependency Check reports
- **Build Artifacts**: JAR files and Docker images

## Monitoring

### Workflow Status
- Check Actions tab in GitHub repository
- Review workflow run logs
- Download artifacts for detailed analysis

### Notifications
- PR comments with test results
- Workflow status notifications
- Security vulnerability alerts

## Customization

### Adding New Jobs
1. Create new workflow file in `.github/workflows/`
2. Define triggers and environment
3. Add steps for your specific needs

### Modifying Existing Workflows
1. Edit the workflow YAML files
2. Update configuration files as needed
3. Test changes in a branch first

### Environment-Specific Deployments
1. Add environment secrets in GitHub
2. Configure deployment steps in `deploy.yml`
3. Set up environment protection rules

## Troubleshooting

### Common Issues
1. **MongoDB Connection**: Ensure MongoDB service is running in workflow
2. **Dependency Issues**: Check OWASP suppressions for false positives
3. **Build Failures**: Review Gradle cache and dependency resolution
4. **Test Failures**: Check test environment configuration

### Debugging
- Enable debug logging in workflows
- Review workflow run logs
- Check artifact contents
- Verify environment variables

## Best Practices

1. **Keep workflows fast**: Use caching and parallel jobs
2. **Fail fast**: Run critical checks first
3. **Security first**: Always run security scans
4. **Document changes**: Update this file when modifying workflows
5. **Test locally**: Verify changes work locally before pushing 