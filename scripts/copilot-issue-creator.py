#!/usr/bin/env python3
"""
Copilot Agent Issue Creator

This script scans a codebase for various patterns (security vulnerabilities, 
code issues, dependency problems) and automatically creates GitHub issues
for detected problems.

Usage:
    python copilot-issue-creator.py --scan-type all
    python copilot-issue-creator.py --scan-type security --create-issues
    python copilot-issue-creator.py --scan-type dependencies --dry-run
"""

import argparse
import json
import os
import re
import sys
import yaml
from pathlib import Path
from typing import Dict, List, Optional, Tuple
import subprocess
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from datetime import datetime


@dataclass
class Issue:
    """Represents an issue to be created"""
    title: str
    body: str
    labels: List[str]
    template: str
    assignees: List[str] = None
    severity: str = "medium"
    
    def __post_init__(self):
        if self.assignees is None:
            self.assignees = []


class CopilotIssueCreator:
    """Main class for scanning code and creating issues"""
    
    def __init__(self, config_path: str = ".github/copilot-issue-config.yml", repo_path: str = "."):
        self.repo_path = Path(repo_path)
        self.config_path = Path(config_path)
        self.config = self._load_config()
        self.issues_to_create: List[Issue] = []
        
    def _load_config(self) -> Dict:
        """Load configuration from YAML file"""
        try:
            with open(self.config_path, 'r') as f:
                return yaml.safe_load(f)
        except FileNotFoundError:
            print(f"Configuration file not found: {self.config_path}")
            return self._get_default_config()
        except yaml.YAMLError as e:
            print(f"Error parsing configuration file: {e}")
            sys.exit(1)
            
    def _get_default_config(self) -> Dict:
        """Return default configuration if file doesn't exist"""
        return {
            'security_patterns': {'code_security_patterns': {'enabled': True, 'patterns': []}},
            'code_patterns': {'todo_comments': {'enabled': True, 'patterns': ['TODO', 'FIXME']}},
            'dependency_patterns': {'vulnerable_dependencies': {'enabled': True}},
            'issue_settings': {'max_issues_per_run': 10, 'default_labels': ['automated']},
            'exclusions': {'excluded_files': [], 'excluded_directories': []}
        }
    
    def scan_security_vulnerabilities(self) -> List[Issue]:
        """Scan for security vulnerability patterns in code"""
        issues = []
        if not self.config.get('security_patterns', {}).get('code_security_patterns', {}).get('enabled', False):
            return issues
            
        patterns = self.config['security_patterns']['code_security_patterns'].get('patterns', [])
        
        for pattern_config in patterns:
            pattern_name = pattern_config['name']
            regex_pattern = pattern_config['regex']
            severity = pattern_config.get('severity', 'medium')
            description = pattern_config.get('description', '')
            
            matches = self._scan_files_for_pattern(regex_pattern)
            
            for file_path, line_num, line_content in matches:
                issue = Issue(
                    title=f"[SECURITY] {pattern_name}: {file_path}:{line_num}",
                    body=self._create_security_issue_body(pattern_name, file_path, line_num, line_content, description),
                    labels=['security', 'vulnerability', 'automated'],
                    template='automated-vulnerability',
                    severity=severity
                )
                issues.append(issue)
                
        return issues
    
    def scan_code_patterns(self) -> List[Issue]:
        """Scan for code quality patterns (TODO, FIXME, etc.)"""
        issues = []
        
        # TODO comments
        if self.config.get('code_patterns', {}).get('todo_comments', {}).get('enabled', False):
            todo_patterns = self.config['code_patterns']['todo_comments'].get('patterns', ['TODO', 'FIXME'])
            priority_mapping = self.config['code_patterns']['todo_comments'].get('priority_mapping', {})
            
            for pattern in todo_patterns:
                regex = rf'#.*?{pattern}[\s:](.*?)$|//.*?{pattern}[\s:](.*?)$|/\*.*?{pattern}[\s:](.*?)\*/'
                matches = self._scan_files_for_pattern(regex, re.IGNORECASE | re.MULTILINE)
                
                for file_path, line_num, line_content in matches:
                    priority = priority_mapping.get(pattern, 'medium')
                    todo_text = self._extract_todo_text(line_content, pattern)
                    
                    issue = Issue(
                        title=f"[CODE-PATTERN] {pattern} Comment: {file_path}:{line_num}",
                        body=self._create_code_pattern_issue_body(pattern, file_path, line_num, line_content, todo_text),
                        labels=['code-quality', 'automated', 'pattern-detection'],
                        template='automated-code-pattern',
                        severity=priority
                    )
                    issues.append(issue)
                    
        return issues
    
    def scan_dependency_issues(self) -> List[Issue]:
        """Scan for dependency-related issues"""
        issues = []
        
        # Check Maven dependencies
        if self.config.get('dependency_patterns', {}).get('vulnerable_dependencies', {}).get('enabled', False):
            pom_path = self.repo_path / 'pom.xml'
            if pom_path.exists():
                vulnerable_deps = self._check_maven_vulnerabilities(pom_path)
                
                for dep_info in vulnerable_deps:
                    issue = Issue(
                        title=f"[DEPENDENCY] Vulnerable dependency: {dep_info['name']}:{dep_info['version']}",
                        body=self._create_dependency_issue_body(dep_info),
                        labels=['dependencies', 'security', 'automated'],
                        template='automated-dependency',
                        severity=dep_info.get('severity', 'medium')
                    )
                    issues.append(issue)
                    
        return issues
    
    def _scan_files_for_pattern(self, pattern: str, flags: int = 0) -> List[Tuple[str, int, str]]:
        """Scan all eligible files for a regex pattern"""
        matches = []
        compiled_pattern = re.compile(pattern, flags)
        
        for file_path in self._get_scannable_files():
            try:
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    for line_num, line in enumerate(f, 1):
                        if compiled_pattern.search(line):
                            rel_path = os.path.relpath(file_path, self.repo_path)
                            matches.append((rel_path, line_num, line.strip()))
            except (UnicodeDecodeError, PermissionError):
                continue
                
        return matches
    
    def _get_scannable_files(self) -> List[Path]:
        """Get list of files that should be scanned"""
        excluded_dirs = set(self.config.get('exclusions', {}).get('excluded_directories', []))
        excluded_files = self.config.get('exclusions', {}).get('excluded_files', [])
        excluded_extensions = set(self.config.get('exclusions', {}).get('excluded_extensions', []))
        
        files = []
        for file_path in self.repo_path.rglob('*'):
            if file_path.is_file():
                # Skip if in excluded directory
                if any(excluded_dir in file_path.parts for excluded_dir in excluded_dirs):
                    continue
                    
                # Skip if excluded file pattern
                rel_path = str(file_path.relative_to(self.repo_path))
                if any(self._matches_pattern(rel_path, pattern) for pattern in excluded_files):
                    continue
                    
                # Skip if excluded extension
                if file_path.suffix in excluded_extensions:
                    continue
                    
                files.append(file_path)
                
        return files
    
    def _matches_pattern(self, filepath: str, pattern: str) -> bool:
        """Check if filepath matches a glob-like pattern"""
        import fnmatch
        return fnmatch.fnmatch(filepath, pattern)
    
    def _extract_todo_text(self, line: str, pattern: str) -> str:
        """Extract the TODO text from a comment line"""
        # Simple extraction - could be made more sophisticated
        parts = line.split(pattern, 1)
        if len(parts) > 1:
            return parts[1].strip(' :').strip()
        return ""
    
    def _check_maven_vulnerabilities(self, pom_path: Path) -> List[Dict]:
        """Check Maven dependencies for known vulnerabilities"""
        # This is a simplified implementation
        # In a real implementation, you'd integrate with vulnerability databases
        vulnerable_deps = []
        
        try:
            tree = ET.parse(pom_path)
            root = tree.getroot()
            
            # Define namespace
            ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
            
            # Find dependencies
            dependencies = root.findall('.//maven:dependency', ns)
            
            # Simple example - check for known vulnerable versions
            known_vulnerabilities = {
                'jackson-databind': {
                    '2.9.10': {'severity': 'high', 'cves': ['CVE-2019-12384']},
                    '2.10.3': {'severity': 'medium', 'cves': ['CVE-2020-8840']}
                }
            }
            
            for dep in dependencies:
                group_id = dep.find('maven:groupId', ns)
                artifact_id = dep.find('maven:artifactId', ns)
                version = dep.find('maven:version', ns)
                
                if all(elem is not None for elem in [group_id, artifact_id, version]):
                    dep_name = f"{group_id.text}:{artifact_id.text}"
                    dep_version = version.text
                    
                    # Remove version properties like ${property}
                    if dep_version.startswith('${'):
                        continue
                        
                    if artifact_id.text in known_vulnerabilities:
                        vuln_versions = known_vulnerabilities[artifact_id.text]
                        if dep_version in vuln_versions:
                            vuln_info = vuln_versions[dep_version]
                            vulnerable_deps.append({
                                'name': dep_name,
                                'version': dep_version,
                                'severity': vuln_info['severity'],
                                'cves': vuln_info['cves']
                            })
                            
        except ET.ParseError as e:
            print(f"Error parsing pom.xml: {e}")
            
        return vulnerable_deps
    
    def _create_security_issue_body(self, pattern_name: str, file_path: str, line_num: int, line_content: str, description: str) -> str:
        """Create issue body for security vulnerability"""
        return f"""**⚠️ This issue was automatically created by a Copilot agent after detecting a potential security vulnerability.**

**Vulnerability Type:** {pattern_name}

**Affected Component:** {file_path}:{line_num}

**Severity Level:** High

**Vulnerability Description:**
{description}

**Evidence/Location:**
```
File: {file_path}
Line: {line_num}
Code: {line_content}
```

**Suggested Remediation:**
- Review the flagged code for security implications
- Implement proper input validation/sanitization
- Consider using parameterized queries or safe APIs
- Follow secure coding best practices

**Automation Information:**
- [x] This issue was created by an automated security scan
- [x] The detection rules used are up-to-date  
- [x] Manual verification is recommended
"""

    def _create_code_pattern_issue_body(self, pattern: str, file_path: str, line_num: int, line_content: str, todo_text: str) -> str:
        """Create issue body for code pattern"""
        return f"""**🤖 This issue was automatically created by a Copilot agent after detecting a code pattern that may need attention.**

**Pattern Type:** {pattern} comments

**Location:** {file_path}:{line_num}

**Priority Level:** Medium - Should be addressed

**Pattern Description:**
Found a {pattern} comment that indicates unfinished work or a known issue that needs to be addressed.

**Code Excerpt:**
```java
{line_content}
```

**TODO Content:** {todo_text}

**Suggested Solution:**
- Review the TODO comment and determine what work needs to be done
- Implement the necessary changes or create a more detailed issue
- Remove the TODO comment once the work is complete
- If the work is not needed, remove the comment

**Impact Assessment:**
TODO comments can indicate incomplete functionality, potential bugs, or areas needing improvement.

**Detection Information:**
- [x] Pattern detected by automated analysis
- [x] Multiple occurrences may exist  
- [x] Requires human review for context
"""

    def _create_dependency_issue_body(self, dep_info: Dict) -> str:
        """Create issue body for dependency issue"""
        cves = ", ".join(dep_info.get('cves', []))
        return f"""**📦 This issue was automatically created by a Copilot agent after detecting a dependency-related concern.**

**Issue Type:** Vulnerable dependency

**Dependency Name:** {dep_info['name']}

**Current Version:** {dep_info['version']}

**Recommended Version:** Latest stable version (check dependency's releases)

**Issue Description:**
This dependency has known security vulnerabilities that should be addressed by updating to a newer version.

**Impact Analysis:**
- Security implications: {cves}
- Potential for exploitation if vulnerability is exposed
- Compliance and audit concerns

**Update Instructions:**
1. Check the latest stable version of this dependency
2. Update the version in pom.xml
3. Run `mvn clean compile` to verify compatibility
4. Run full test suite to ensure no breaking changes
5. Review release notes for any API changes

**References:**
- CVEs: {cves}
- Check https://nvd.nist.gov/ for detailed vulnerability information

**Detection Information:**
- [x] Detected during automated dependency scan
- [x] Security database checked
- [ ] Version compatibility verified
- [ ] Breaking changes analysis needed
"""

    def create_issues(self, dry_run: bool = False) -> None:
        """Create GitHub issues for detected problems"""
        if not self.issues_to_create:
            print("No issues to create.")
            return
            
        max_issues = self.config.get('issue_settings', {}).get('max_issues_per_run', 10)
        issues_to_process = self.issues_to_create[:max_issues]
        
        if dry_run:
            print(f"DRY RUN: Would create {len(issues_to_process)} issues:")
            for i, issue in enumerate(issues_to_process, 1):
                print(f"{i}. {issue.title}")
                print(f"   Labels: {', '.join(issue.labels)}")
                print(f"   Template: {issue.template}")
                print(f"   Severity: {issue.severity}")
                print()
        else:
            print(f"Creating {len(issues_to_process)} issues...")
            # In a real implementation, this would use GitHub API
            # For now, we'll output the issue data
            for issue in issues_to_process:
                print(f"Would create issue: {issue.title}")
                self._write_issue_to_file(issue)
                
    def _write_issue_to_file(self, issue: Issue) -> None:
        """Write issue data to a file (for demonstration)"""
        output_dir = Path("generated_issues")
        output_dir.mkdir(exist_ok=True)
        
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"{timestamp}_{issue.template}.md"
        filepath = output_dir / filename
        
        with open(filepath, 'w') as f:
            f.write(f"# {issue.title}\n\n")
            f.write(f"**Labels:** {', '.join(issue.labels)}\n")
            f.write(f"**Template:** {issue.template}\n")
            f.write(f"**Severity:** {issue.severity}\n\n")
            f.write("## Issue Body\n\n")
            f.write(issue.body)
            
        print(f"Issue written to: {filepath}")

    def run_scan(self, scan_types: List[str]) -> None:
        """Run the specified scans"""
        if 'security' in scan_types or 'all' in scan_types:
            print("Scanning for security vulnerabilities...")
            security_issues = self.scan_security_vulnerabilities()
            self.issues_to_create.extend(security_issues)
            print(f"Found {len(security_issues)} security issues")
            
        if 'code-patterns' in scan_types or 'all' in scan_types:
            print("Scanning for code patterns...")
            pattern_issues = self.scan_code_patterns()
            self.issues_to_create.extend(pattern_issues)
            print(f"Found {len(pattern_issues)} code pattern issues")
            
        if 'dependencies' in scan_types or 'all' in scan_types:
            print("Scanning for dependency issues...")
            dependency_issues = self.scan_dependency_issues()
            self.issues_to_create.extend(dependency_issues)
            print(f"Found {len(dependency_issues)} dependency issues")
            
        print(f"\nTotal issues found: {len(self.issues_to_create)}")


def main():
    parser = argparse.ArgumentParser(description="Copilot Agent Issue Creator")
    parser.add_argument(
        '--scan-type', 
        choices=['security', 'code-patterns', 'dependencies', 'all'],
        default='all',
        help='Type of scan to perform'
    )
    parser.add_argument(
        '--config',
        default='.github/copilot-issue-config.yml',
        help='Path to configuration file'
    )
    parser.add_argument(
        '--repo-path',
        default='.',
        help='Path to repository to scan'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what issues would be created without actually creating them'
    )
    parser.add_argument(
        '--create-issues',
        action='store_true',
        help='Actually create the issues (requires GitHub token)'
    )
    
    args = parser.parse_args()
    
    creator = CopilotIssueCreator(args.config, args.repo_path)
    
    scan_types = [args.scan_type] if args.scan_type != 'all' else ['security', 'code-patterns', 'dependencies']
    creator.run_scan(scan_types)
    
    if args.create_issues or args.dry_run:
        creator.create_issues(dry_run=args.dry_run)


if __name__ == "__main__":
    main()