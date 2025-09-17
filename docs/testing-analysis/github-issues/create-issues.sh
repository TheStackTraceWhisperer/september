#!/bin/bash
# GitHub Issue Creation Script for September Engine Testing Analysis
# 
# This script provides commands to create all testing task force issues.
# Run each command individually or customize as needed.
#
# Prerequisites:
# - Install GitHub CLI: https://cli.github.com/
# - Authenticate: gh auth login
# - Navigate to repository root
#

set -e

echo "🚀 September Engine Testing Task Force - GitHub Issues Creation"
echo "================================================================"
echo ""

# Check if gh CLI is available
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI not found. Please install: https://cli.github.com/"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "❌ Not authenticated with GitHub. Run: gh auth login"
    exit 1
fi

echo "✅ GitHub CLI ready"
echo ""

# Create each issue

echo "📋 Creating Issue 1/7: 🔴 Engine Systems Testing - Critical Priority"
gh issue create \
    --title "🔴 Engine Systems Testing - Critical Priority" \
    --body-file "docs/testing-analysis/github-issues/issue-01-engine-systems-testing-github.md" \
    --label "priority:critical" --label "testing" --label "type:enhancement" --label "component:systems"

echo "✅ Created: 🔴 Engine Systems Testing - Critical Priority"
echo ""

echo "📋 Creating Issue 2/7: 🟡 Audio Package Testing - High Priority"
gh issue create \
    --title "🟡 Audio Package Testing - High Priority" \
    --body-file "docs/testing-analysis/github-issues/issue-02-audio-package-testing-github.md" \
    --label "priority:high" --label "testing" --label "type:enhancement" --label "component:audio"

echo "✅ Created: 🟡 Audio Package Testing - High Priority"
echo ""

echo "📋 Creating Issue 3/7: 🟡 Input System Testing - High Priority"
gh issue create \
    --title "🟡 Input System Testing - High Priority" \
    --body-file "docs/testing-analysis/github-issues/issue-03-input-system-testing-github.md" \
    --label "priority:high" --label "testing" --label "type:enhancement" --label "component:input"

echo "✅ Created: 🟡 Input System Testing - High Priority"
echo ""

echo "📋 Creating Issue 4/7: 🟢 Asset Management Testing - Medium Priority"
gh issue create \
    --title "🟢 Asset Management Testing - Medium Priority" \
    --body-file "docs/testing-analysis/github-issues/issue-04-asset-management-testing-github.md" \
    --label "priority:medium" --label "testing" --label "type:enhancement" --label "component:assets"

echo "✅ Created: 🟢 Asset Management Testing - Medium Priority"
echo ""

echo "📋 Creating Issue 5/7: 🟢 State Management Testing - Medium Priority"
gh issue create \
    --title "🟢 State Management Testing - Medium Priority" \
    --body-file "docs/testing-analysis/github-issues/issue-05-state-management-testing-github.md" \
    --label "priority:medium" --label "testing" --label "type:enhancement" --label "component:state"

echo "✅ Created: 🟢 State Management Testing - Medium Priority"
echo ""

echo "📋 Creating Issue 6/7: 🟢 Core Engine Services Testing - Medium Priority"
gh issue create \
    --title "🟢 Core Engine Services Testing - Medium Priority" \
    --body-file "docs/testing-analysis/github-issues/issue-06-core-services-testing-github.md" \
    --label "priority:medium" --label "testing" --label "type:enhancement" --label "component:core"

echo "✅ Created: 🟢 Core Engine Services Testing - Medium Priority"
echo ""

echo "📋 Creating Issue 7/7: 🔵 Advanced Rendering Components Testing - Low Priority"
gh issue create \
    --title "🔵 Advanced Rendering Components Testing - Low Priority" \
    --body-file "docs/testing-analysis/github-issues/issue-07-advanced-rendering-testing-github.md" \
    --label "priority:low" --label "testing" --label "type:enhancement" --label "component:rendering"

echo "✅ Created: 🔵 Advanced Rendering Components Testing - Low Priority"
echo ""

echo "🎉 All testing task force issues created!"
echo ""
echo "📌 Next Steps:"
echo "1. Review and assign issues to team members"
echo "2. Set up task force coordination"
echo "3. Begin parallel development"
echo "4. Monitor progress and coverage improvements"
