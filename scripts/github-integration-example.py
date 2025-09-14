#!/usr/bin/env python3
"""
GitHub API Integration Example for Copilot Issue Creator

This script demonstrates how to actually create GitHub issues using the GitHub API
when integrated with the copilot-issue-creator.py script.
"""

import os
import requests
import json
from typing import Dict, Any

class GitHubIssueCreator:
    """Creates GitHub issues via the REST API"""
    
    def __init__(self, repo_owner: str, repo_name: str, token: str):
        self.repo_owner = repo_owner
        self.repo_name = repo_name
        self.token = token
        self.base_url = "https://api.github.com"
        self.headers = {
            "Authorization": f"token {token}",
            "Accept": "application/vnd.github.v3+json",
            "Content-Type": "application/json"
        }
    
    def create_issue(self, title: str, body: str, labels: list = None, assignees: list = None) -> Dict[str, Any]:
        """Create a new GitHub issue"""
        url = f"{self.base_url}/repos/{self.repo_owner}/{self.repo_name}/issues"
        
        issue_data = {
            "title": title,
            "body": body
        }
        
        if labels:
            issue_data["labels"] = labels
        if assignees:
            issue_data["assignees"] = assignees
            
        response = requests.post(url, headers=self.headers, json=issue_data)
        
        if response.status_code == 201:
            return response.json()
        else:
            response.raise_for_status()
    
    def check_existing_issue(self, title: str) -> bool:
        """Check if an issue with similar title already exists"""
        url = f"{self.base_url}/repos/{self.repo_owner}/{self.repo_name}/issues"
        params = {
            "state": "open",
            "labels": "automated",
            "per_page": 100
        }
        
        response = requests.get(url, headers=self.headers, params=params)
        
        if response.status_code == 200:
            issues = response.json()
            for issue in issues:
                if issue["title"] == title:
                    return True
        
        return False
    
    def trigger_copilot_scan(self, scan_type: str = "all", create_issues: bool = False, max_issues: str = "5") -> Dict[str, Any]:
        """Trigger the Copilot Issue Creator workflow"""
        url = f"{self.base_url}/repos/{self.repo_owner}/{self.repo_name}/actions/workflows/copilot-issue-creator.yml/dispatches"
        
        workflow_data = {
            "ref": "main",
            "inputs": {
                "scan_type": scan_type,
                "create_issues": str(create_issues).lower(),
                "max_issues": max_issues
            }
        }
        
        response = requests.post(url, headers=self.headers, json=workflow_data)
        
        if response.status_code == 204:
            return {"success": True, "message": "Workflow triggered successfully"}
        else:
            response.raise_for_status()


def main():
    """Example usage of the GitHub API integration"""
    
    # Get configuration from environment variables
    repo_owner = os.getenv("GITHUB_REPOSITORY_OWNER", "TheStackTraceWhisperer")
    repo_name = os.getenv("GITHUB_REPOSITORY_NAME", "september")
    token = os.getenv("GITHUB_TOKEN")
    
    if not token:
        print("Error: GITHUB_TOKEN environment variable is required")
        return
    
    # Create GitHub API client
    github = GitHubIssueCreator(repo_owner, repo_name, token)
    
    # Example 1: Trigger a security scan
    print("Triggering security vulnerability scan...")
    try:
        result = github.trigger_copilot_scan(scan_type="security", create_issues=False)
        print(f"Success: {result['message']}")
    except Exception as e:
        print(f"Error: {e}")
    
    # Example 2: Check for existing issues before creating new ones
    test_title = "[SECURITY] SQL Injection Risk: example.java:42"
    if github.check_existing_issue(test_title):
        print(f"Issue already exists: {test_title}")
    else:
        print(f"No existing issue found for: {test_title}")
    
    # Example 3: Create a sample issue (commented out to avoid spam)
    """
    sample_issue = {
        "title": "[DEMO] Copilot Issue Creator Test",
        "body": "This is a demonstration issue created by the Copilot Issue Creator system.",
        "labels": ["automated", "demo", "copilot-agent"]
    }
    
    try:
        issue = github.create_issue(**sample_issue)
        print(f"Created issue #{issue['number']}: {issue['title']}")
        print(f"URL: {issue['html_url']}")
    except Exception as e:
        print(f"Error creating issue: {e}")
    """


if __name__ == "__main__":
    main()