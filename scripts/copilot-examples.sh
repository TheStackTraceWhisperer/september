#!/bin/bash
# 
# Copilot Agent Usage Examples
# This script demonstrates how Copilot agents can use the issue creation system
#

set -e

echo "🤖 Copilot Agent Issue Creation Examples"
echo "========================================"

# Example 1: Basic security scan
echo ""
echo "📋 Example 1: Security vulnerability scan"
echo "Command: gh workflow run copilot-issue-creator.yml -f scan_type=security -f create_issues=false"
echo "Usage: Scan for security vulnerabilities without creating issues (dry run)"

# Example 2: Create issues for TODO comments
echo ""
echo "📋 Example 2: TODO comment tracking"
echo "Command: gh workflow run copilot-issue-creator.yml -f scan_type=code-patterns -f create_issues=true -f max_issues=3"
echo "Usage: Find TODO/FIXME comments and create tracking issues"

# Example 3: Dependency audit
echo ""
echo "📋 Example 3: Dependency vulnerability audit"
echo "Command: gh workflow run copilot-issue-creator.yml -f scan_type=dependencies -f create_issues=true -f max_issues=5"
echo "Usage: Check dependencies for vulnerabilities and create issues"

# Example 4: Full comprehensive scan
echo ""
echo "📋 Example 4: Comprehensive scan"
echo "Command: gh workflow run copilot-issue-creator.yml -f scan_type=all -f create_issues=false"
echo "Usage: Scan everything but don't create issues (assessment only)"

# Example 5: Direct script usage
echo ""
echo "📋 Example 5: Direct script usage (local)"
echo "Command: python scripts/copilot-issue-creator.py --scan-type all --dry-run"
echo "Usage: Run scan locally for development/testing"

echo ""
echo "🔧 Configuration"
echo "==============="
echo "• Edit .github/copilot-issue-config.yml to customize patterns"
echo "• Modify issue templates in .github/ISSUE_TEMPLATE/"
echo "• Adjust rate limits and exclusions as needed"

echo ""
echo "📊 Monitoring"
echo "============"
echo "• Check GitHub Actions for workflow results"
echo "• Review generated artifacts for scan details"
echo "• Monitor created issues for false positives"

echo ""
echo "🎯 Prompt Examples for Copilot Agents"
echo "====================================="

cat << 'EOF'

1. "Open new issues for each occurrence of TODO or FIXME comments"
   → Triggers: scan_type=code-patterns, create_issues=true

2. "Open new issues for each dependency having vulnerable dependencies"  
   → Triggers: scan_type=dependencies, create_issues=true

3. "Scan the codebase for security vulnerabilities and create tracking issues"
   → Triggers: scan_type=security, create_issues=true

4. "Find all code patterns that need attention and create issues for them"
   → Triggers: scan_type=all, create_issues=true, max_issues=10

5. "Check for outdated dependencies and security issues"
   → Triggers: scan_type=dependencies, create_issues=false (assessment only)

EOF

echo ""
echo "✅ System is ready for Copilot agent use!"
echo "   See docs/copilot-issue-creation.md for detailed documentation"