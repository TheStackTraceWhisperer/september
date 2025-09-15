# Copilot Agent Issue Creation

Simple system for GitHub Copilot agents to create issues using GitHub Actions.

## Usage

Copilot agents can create issues by triggering the workflow:

```bash
gh workflow run copilot-issue-creator.yml \
  -f title="Fix security vulnerability in authentication" \
  -f body="Found potential SQL injection in login.java line 42" \
  -f labels="security,bug" \
  -f assignees="developer1"
```

Or via REST API:

```bash
curl -X POST \
  -H "Authorization: token $GITHUB_TOKEN" \
  https://api.github.com/repos/owner/repo/actions/workflows/copilot-issue-creator.yml/dispatches \
  -d '{
    "ref": "main",
    "inputs": {
      "title": "Update deprecated API usage",
      "body": "Replace deprecated methods in UserService.java",
      "labels": "enhancement,maintenance"
    }
  }'
```

## Parameters

- **title** (required): Issue title
- **body** (required): Issue description
- **labels** (optional): Comma-separated labels
- **assignees** (optional): Comma-separated GitHub usernames

## Examples

**Security Issue:**
```bash
gh workflow run copilot-issue-creator.yml \
  -f title="Security: Hardcoded credentials detected" \
  -f body="Found hardcoded API key in config.properties" \
  -f labels="security,critical"
```

**Code Quality:**
```bash
gh workflow run copilot-issue-creator.yml \
  -f title="Code Quality: TODO comment needs attention" \
  -f body="TODO comment at src/main/Main.java:15 needs implementation" \
  -f labels="code-quality,enhancement"
```

**Dependency Issue:**
```bash
gh workflow run copilot-issue-creator.yml \
  -f title="Dependency: Update vulnerable library" \
  -f body="Library xyz-1.2.3 has known security vulnerabilities" \
  -f labels="dependencies,security"
```

The system is intentionally simple - Copilot agents provide the content, and GitHub Actions handles the issue creation.