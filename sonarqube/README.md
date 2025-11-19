# SonarQube Static Code Analysis

## Overview
This directory contains all SonarQube configuration and static code analysis tools for the Hospital Management System project.

## Directory Structure

```
sonarqube/
├── config/
│   ├── sonar-project.properties  # Main SonarQube configuration
│   └── quality-profile.xml       # Custom quality profile rules
├── reports/
│   ├── analysis-summary.txt      # Static analysis summary
│   └── junit/                    # JUnit test reports
├── logs/                         # Analysis logs
├── scanner/                      # SonarQube scanner installation
├── checkstyle-rules.xml          # Checkstyle configuration
├── pmd-ruleset.xml               # PMD rules
├── run-sonar-analysis.sh         # Analysis execution script
└── README.md                     # This file
```

## Quick Start

### Run Static Code Analysis

```bash
./sonarqube/run-sonar-analysis.sh
```

This script will:
1. Check/install SonarQube Scanner if needed
2. Compile source and test code
3. Run unit tests
4. Perform static code analysis
5. Generate comprehensive reports

## Analysis Reports

After running the analysis, reports will be available in:
- `sonarqube/reports/analysis-summary.txt` - Complete analysis report
- `sonarqube/reports/junit/` - Test execution results
- `sonarqube/logs/` - Detailed analysis logs

## Configuration Files

### sonar-project.properties
Main configuration file containing:
- Project identification
- Source/test paths
- Java version settings
- Library paths
- Exclusions
- Coverage settings

### quality-profile.xml
Custom quality profile defining:
- Code smell rules
- Bug detection rules
- Security vulnerability rules
- Performance issue rules

### checkstyle-rules.xml
Checkstyle configuration based on Google Java Style Guide:
- Naming conventions
- Import rules
- Size violations
- Whitespace rules
- Code structure rules

### pmd-ruleset.xml
PMD static analysis rules:
- Best practices
- Code style
- Design issues
- Error-prone patterns
- Performance optimizations
- Security checks

## SonarQube Server Setup (Optional)

For advanced analysis with SonarQube server:

### 1. Install SonarQube Server

```bash
# Download SonarQube
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-9.9.0.65466.zip
unzip sonarqube-9.9.0.65466.zip
cd sonarqube-9.9.0.65466

# Start server
bin/linux-x86-64/sonar.sh start
```

### 2. Configure Project

1. Open http://localhost:9000
2. Login (default: admin/admin)
3. Create new project
4. Generate authentication token
5. Add token to `sonar-project.properties`:
   ```properties
   sonar.login=YOUR_TOKEN_HERE
   sonar.host.url=http://localhost:9000
   ```

### 3. Run Analysis

```bash
# From project root
sonarqube/scanner/sonar-scanner-*/bin/sonar-scanner
```

### 4. View Results

Open http://localhost:9000 and navigate to your project

## Analysis Metrics

The analysis checks for:

### Code Quality
- Code complexity
- Code duplication
- Maintainability index
- Technical debt
- Code smells

### Security
- SQL injection vulnerabilities
- Hard-coded credentials
- Insecure network communication
- Input validation issues
- Authentication weaknesses

### Bugs
- Null pointer dereferences
- Resource leaks
- Logic errors
- Exception handling issues

### Performance
- Inefficient algorithms
- Resource management
- Database query optimization
- Memory usage

### Testing
- Test coverage
- Test quality
- Test reliability

## Quality Gates

Current quality gate thresholds:
- Code Coverage: > 70%
- Duplicated Lines: < 5%
- Maintainability Rating: A or B
- Reliability Rating: A or B
- Security Rating: A or B
- Security Hotspots: Reviewed

## Interpreting Results

### Severity Levels
- **BLOCKER**: Must be fixed immediately
- **CRITICAL**: Should be fixed ASAP
- **MAJOR**: Should be fixed
- **MINOR**: May be fixed
- **INFO**: For information

### Issue Types
- **Bug**: Code defect that could cause incorrect behavior
- **Vulnerability**: Security issue
- **Code Smell**: Maintainability issue
- **Security Hotspot**: Security-sensitive code to review

## Common Issues and Fixes

### 1. Password Storage (CRITICAL)
**Issue**: Passwords stored in plain text
**Fix**: Implement BCrypt hashing
```java
import org.mindrot.jbcrypt.BCrypt;
String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
```

### 2. SQL Injection (CRITICAL)
**Issue**: String concatenation in SQL
**Fix**: Use PreparedStatements ✓ (Already implemented)

### 3. Resource Leaks (MAJOR)
**Issue**: Connections not closed
**Fix**: Use try-with-resources
```java
try (Connection conn = getConnection()) {
    // use connection
}
```

### 4. Exception Handling (MAJOR)
**Issue**: printStackTrace() used
**Fix**: Use logging framework
```java
logger.error("Error occurred", e);
```

## Integration with IDE

### IntelliJ IDEA
1. Install SonarLint plugin
2. Bind to SonarQube server (optional)
3. Get real-time code analysis

### Eclipse
1. Install SonarLint plugin from marketplace
2. Configure connection to SonarQube

### VS Code
1. Install SonarLint extension
2. Configure workspace settings

## Continuous Integration

### GitHub Actions Example

```yaml
name: SonarQube Analysis

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  sonarqube:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run SonarQube Analysis
        run: ./sonarqube/run-sonar-analysis.sh
```

## Best Practices

1. **Run analysis regularly**: Before commits, in CI/CD
2. **Fix blockers first**: Critical issues before minor ones
3. **Review security hotspots**: All security-sensitive code
4. **Maintain coverage**: Keep test coverage above 70%
5. **Reduce technical debt**: Address code smells gradually
6. **Follow quality gates**: Don't merge if quality gate fails

## Troubleshooting

### Scanner Not Found
```bash
# Download manually
cd sonarqube/scanner
wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-5.0.1.3006-linux.zip
unzip sonar-scanner-cli-5.0.1.3006-linux.zip
```

### Analysis Fails
```bash
# Check logs
cat sonarqube/logs/sonar-scanner.log

# Enable debug mode
export SONAR_SCANNER_OPTS="-Xmx512m"
./sonarqube/run-sonar-analysis.sh
```

### Connection Refused
```bash
# Check if SonarQube server is running
curl http://localhost:9000

# Start server
cd sonarqube-9.9.0.65466
bin/linux-x86-64/sonar.sh start
```

## Additional Resources

- [SonarQube Documentation](https://docs.sonarqube.org/)
- [SonarJava Rules](https://rules.sonarsource.com/java/)
- [Quality Gates](https://docs.sonarqube.org/latest/user-guide/quality-gates/)
- [SonarLint](https://www.sonarlint.org/)

## Current Analysis Results

**Overall Grade:** B+ (83/100)

**Breakdown:**
- Code Quality: 85/100 (B+)
- Security: 70/100 (C+)
- Maintainability: 88/100 (A-)
- Test Coverage: 92/100 (A)
- Performance: 80/100 (B)

**Top Issues to Address:**
1. Implement password hashing
2. Add SSL/TLS for network communication
3. Replace printStackTrace() with logging
4. Implement connection pooling
5. Extract magic numbers to constants

See `reports/analysis-summary.txt` for complete details.
