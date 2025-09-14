# Copilot Agent Issue Creation System

This system provides a stable and robust way for GitHub Copilot agents to automatically create issues based on detected patterns in the codebase.

## 🎯 Purpose

The Copilot Issue Creation System addresses the problem statement:
> "Determine a stable and robust way for Copilot agents to CREATE new copilot-assigned issues."

It enables automated detection and issue creation for:
- Security vulnerabilities in dependencies and code
- Code quality patterns (TODO comments, deprecated APIs, etc.)
- Dependency management issues (outdated, vulnerable, unused dependencies)
- Custom patterns defined in configuration

## 🏗️ Architecture

### Components

1. **Issue Templates** (`.github/ISSUE_TEMPLATE/`)
   - `automated-vulnerability.yml` - Security vulnerability issues
   - `automated-code-pattern.yml` - Code quality and pattern issues  
   - `automated-dependency.yml` - Dependency-related issues

2. **Configuration** (`.github/copilot-issue-config.yml`)
   - Defines patterns to scan for
   - Configures severity levels and labels
   - Sets rate limits and exclusions

3. **Scanner Script** (`scripts/copilot-issue-creator.py`)
   - Python script that scans codebase for patterns
   - Generates issue data based on findings
   - Supports multiple scan types and dry-run mode

4. **GitHub Actions Workflow** (`.github/workflows/copilot-issue-creator.yml`)
   - Automates the scanning and issue creation process
   - Provides multiple trigger mechanisms
   - Handles permissions and artifact management

## 🚀 Usage

### For Copilot Agents

Copilot agents can trigger issue creation using the GitHub Actions workflow_dispatch API:

```bash
# Trigger via GitHub CLI
gh workflow run copilot-issue-creator.yml \
  -f scan_type=all \
  -f create_issues=true \
  -f max_issues=5

# Trigger via REST API
curl -X POST \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Authorization: token $GITHUB_TOKEN" \
  https://api.github.com/repos/OWNER/REPO/actions/workflows/copilot-issue-creator.yml/dispatches \
  -d '{"ref":"main","inputs":{"scan_type":"security","create_issues":"true","max_issues":"3"}}'
```

### For Manual Use

1. **Via GitHub Web Interface:**
   - Go to Actions → Copilot Issue Creator → Run workflow
   - Select scan type and parameters
   - Click "Run workflow"

2. **Via Command Line:**
   ```bash
   # Install dependencies
   pip install pyyaml

   # Run dry run scan
   python scripts/copilot-issue-creator.py --scan-type all --dry-run

   # Run specific scan type
   python scripts/copilot-issue-creator.py --scan-type security --dry-run

   # Create issues (requires GitHub token)
   export GITHUB_TOKEN=your_token_here
   python scripts/copilot-issue-creator.py --scan-type dependencies --create-issues
   ```

## 📋 Scan Types

### Security Scans
- **Vulnerable Dependencies**: Checks for known CVEs in Maven dependencies
- **Code Security Patterns**: Scans for common security anti-patterns:
  - SQL injection risks
  - XSS vulnerabilities  
  - Hardcoded credentials
  - Weak cryptography usage

### Code Pattern Scans
- **TODO/FIXME Comments**: Finds incomplete work markers
- **Deprecated API Usage**: Detects use of deprecated methods/classes
- **Performance Anti-patterns**: Identifies inefficient code patterns
- **Code Duplication**: Finds repeated code blocks

### Dependency Scans
- **Outdated Dependencies**: Finds dependencies older than configured threshold
- **Vulnerable Dependencies**: Cross-references with vulnerability databases
- **Unused Dependencies**: Identifies dependencies that aren't being used
- **License Compliance**: Checks for incompatible licenses

## ⚙️ Configuration

### Main Configuration File (`.github/copilot-issue-config.yml`)

```yaml
# Security vulnerability patterns
security_patterns:
  vulnerable_dependencies:
    enabled: true
    severity_threshold: "medium"
  code_security_patterns:
    enabled: true
    patterns:
      - name: "SQL Injection Risk"
        regex: '(Statement|PreparedStatement).*?executeQuery\(.*?\+.*?\)'
        severity: "high"

# Code quality patterns  
code_patterns:
  todo_comments:
    enabled: true
    patterns: ["TODO", "FIXME", "HACK", "XXX"]
    priority_mapping:
      "FIXME": "high"
      "TODO": "medium"

# Issue creation settings
issue_settings:
  max_issues_per_run: 10
  default_labels: ["automated", "copilot-agent"]
  rate_limit:
    max_per_hour: 5
    max_per_day: 20
```

### Customization

You can customize the system by:

1. **Adding new patterns** to the configuration file
2. **Creating custom issue templates** in `.github/ISSUE_TEMPLATE/`
3. **Modifying exclusions** to skip certain files/directories
4. **Adjusting severity levels** and priority mappings
5. **Setting up notifications** (Slack, email) for critical issues

## 🔒 Security and Safety

### Rate Limiting
- Maximum issues per run (default: 10)
- Per-hour and per-day limits
- Duplicate detection to prevent spam

### Permissions
The workflow requires minimal permissions:
- `contents: read` - To read repository files
- `issues: write` - To create issues
- `pull-requests: read` - For PR context

### Safety Features
- **Dry-run mode** by default for testing
- **Pattern validation** before scanning
- **File exclusions** to avoid scanning sensitive files
- **Duplicate prevention** to avoid creating redundant issues

## 📊 Monitoring and Reporting

### Workflow Artifacts
- Scan results are uploaded as artifacts
- Detailed logs for troubleshooting
- Issue templates generated for review

### GitHub Integration
- Workflow summaries show scan results
- PR comments for scan triggered by changes
- Labels and assignees for automatic routing

## 🎯 Examples

### Example 1: Scan for Security Issues
```bash
# Copilot agent command to find security vulnerabilities
python scripts/copilot-issue-creator.py --scan-type security --dry-run
```

**Prompt for Copilot:** "Open new issues for each security vulnerability found in dependencies"

### Example 2: Scan for TODO Comments  
```bash
# Find all TODO/FIXME comments and create tracking issues
python scripts/copilot-issue-creator.py --scan-type code-patterns --create-issues
```

**Prompt for Copilot:** "Open new issues for each occurrence of TODO or FIXME comments"

### Example 3: Dependency Audit
```bash
# Check for outdated or vulnerable dependencies
python scripts/copilot-issue-creator.py --scan-type dependencies --create-issues
```

**Prompt for Copilot:** "Open new issues for each dependency having vulnerable dependencies"

## 🔧 Maintenance

### Regular Tasks
1. **Update vulnerability databases** - Keep security patterns current
2. **Review generated issues** - Ensure quality and relevance  
3. **Adjust configuration** - Tune based on project needs
4. **Monitor rate limits** - Prevent overwhelming the issue tracker

### Troubleshooting

**Common Issues:**

1. **No issues created:** Check configuration and exclusions
2. **Too many issues:** Adjust rate limits and severity thresholds
3. **Missing patterns:** Update regex patterns in configuration
4. **Permission errors:** Verify GitHub token and workflow permissions

**Debug Commands:**
```bash
# Verbose output
python scripts/copilot-issue-creator.py --scan-type all --dry-run -v

# Test configuration
python -c "import yaml; print(yaml.safe_load(open('.github/copilot-issue-config.yml')))"

# Check file exclusions
python scripts/copilot-issue-creator.py --list-files
```

## 📈 Future Enhancements

Potential improvements for the system:
- Integration with external vulnerability databases (Snyk, WhiteSource)
- Machine learning-based pattern detection
- Slack/Teams notifications for critical issues
- Custom webhooks for issue creation events
- Integration with project management tools (Jira, Azure DevOps)
- Automated issue prioritization based on business impact

## 🤝 Contributing

To enhance the Copilot Issue Creation System:

1. **Add new patterns** to the configuration
2. **Create additional issue templates** for specific use cases
3. **Improve the scanner script** with new detection capabilities
4. **Update documentation** with new examples and use cases
5. **Submit feedback** on detected patterns and false positives

## 📄 License

This system is part of the September project and follows the same licensing terms.