# Code Style Guide

Minimal, explicit, example‑driven. If not covered here, follow modern idiomatic Java.

---
## Quick Rules
- 2 spaces indent, no tabs.
- Braces always (except single‑expression lambdas & switch expressions).
- Opening brace end of line; closing brace alone on its own line.
- Allowed inline bridges: `} else {`, `} catch (...) {`, `} finally {`.
- No single‑line statements (`if (x) return;` forbidden).
- Prefer early returns over deep nesting.
- No wildcard imports.
- Keep methods small; avoid gratuitous streams.

---
## 1. Braces
<table>
<tr><th>Bad</th><th>Good</th></tr>
<tr><td><code>if (x) doWork();</code></td><td><pre><code>if (x) {
  doWork();
}</code></pre></td></tr>
<tr><td><code>if (a) { one(); } else two();</code></td><td><pre><code>if (a) {
  one();
} else {
  two();
}</code></pre></td></tr>
<tr><td><code>try { a(); } catch (E e) { h(); }</code></td><td><pre><code>try {
  a();
} catch (E e) {
  h();
}</code></pre></td></tr>
<tr><td><code>try { a(); } catch (E e) { b(); } finally { c(); }</code></td><td><pre><code>try {
  a();
} catch (E e) {
  b();
} finally {
  c();
}</code></pre></td></tr>
</table>

---
## 2. No Single‑Line Statements
<table>
<tr><th>Bad</th><th>Good</th></tr>
<tr><td><code>if (done) return;</code></td><td><pre><code>if (done) {
  return;
}</code></pre></td></tr>
<tr><td><code>while (ready) step();</code></td><td><pre><code>while (ready) {
  step();
}</code></pre></td></tr>
</table>

---
## 3. Indentation & Continuation
- Break fluent chains one call per line when each step adds meaning.
- Align lambdas/parameters for clarity.

```java
var user = repo
  .find(id)
  .orElseThrow(() -> new NotFound(id));
```

---
## 4. Early Returns (Prefer)
<table>
<tr><th>Bad</th><th>Good</th></tr>
<tr><td><pre><code>int parse(String s) {
  if (s != null) {
    if (!s.isBlank()) {
      return Integer.parseInt(s);
    }
  }
  return -1;
}</code></pre></td><td><pre><code>int parse(String s) {
  if (s == null || s.isBlank()) {
    return -1;
  }
  return Integer.parseInt(s);
}</code></pre></td></tr>
</table>

---
## 5. Streams vs Loops
Use loops when trivial; streams when transforming.

```java
// Loop (trivial accumulation)
int sum = 0;
for (int v : values) {
  sum += v;
}

// Stream (pipeline semantics)
var emails = users.stream()
  .filter(User::active)
  .map(User::email)
  .toList();
```

---
## 6. Imports
<table>
<tr><th>Bad</th><th>Good</th></tr>
<tr><td><code>import java.util.*;</code></td><td><code>import java.util.List;</code></td></tr>
</table>

---
## 7. Naming
| Item | Style | Example |
|------|-------|---------|
| Class / Record | PascalCase | `WindowContext` |
| Method / var | camelCase | `createWindow` |
| Constant | UPPER_SNAKE_CASE | `DEFAULT_WIDTH` |

---
## 8. Error Handling
<table>
<tr><th>Bad</th><th>Good</th></tr>
<tr><td><pre><code>catch (Exception e) {}</code></pre></td><td><pre><code>catch (ConfigException e) {
  log.warn("Bad config", e);
}</code></pre></td></tr>
</table>
Avoid broad catches unless rethrowing.

---
## 9. Logging (SLF4J)
<table>
<tr><th>Bad</th><th>Good</th></tr>
<tr><td><code>log.info("Count=" + n);</code></td><td><code>log.info("Count={}", n);</code></td></tr>
</table>
Use DEBUG for verbose internals.

---
## 10. Comments & Docs
- Prefer self‑explanatory code.
- Javadoc only for public API or non‑obvious intent.

---
## 11. Nullability
- Validate at boundaries; keep interiors non‑null by design.

---
## 12. File Order
1. Static fields
2. Instance fields
3. Constructors / factories
4. Public methods
5. Protected
6. Package‑private
7. Private
8. Nested types

---
## 13. Testing
Our testing philosophy is based on high-value integration tests. See `TESTING.md` for full details.

<table>
<tr><th>Bad</th><th>Good</th></tr>
<tr><td>Static mocking native libraries (GLFW, OpenGL)</td><td>Use `EngineTestHarness` for a live context</td></tr>
<tr><td>Testing implementation details (e.g., `verify(method).wasCalled()`)</td><td>Testing observable behavior (e.g., `assertThat(object.getState()).isEqualTo(newState)`)</td></tr>
</table>

---
## 14. Tooling
- `.editorconfig` defines indentation & line endings.
- Spotless: formatting & unused imports.
- Checkstyle: braces, structure.

### Spotless (Maven)
```xml
<plugin>
  <groupId>com.diffplug.spotless</groupId>
  <artifactId>spotless-maven-plugin</artifactId>
  <version>2.43.0</version>
  <configuration>
    <java>
      <palantirJavaFormat version="2.47.0"/>
      <removeUnusedImports/>
      <formatAnnotations/>
      <target>src/main/java/**/*.java,src/test/java/**/*.java</target>
    </java>
  </configuration>
</plugin>
```

### Checkstyle (Excerpt)
```xml
<module name="Checker">
  <module name="TreeWalker">
    <module name="NeedBraces"/>
    <module name="LeftCurly"><property name="option" value="eol"/></module>
    <module name="RightCurly"><property name="option" value="alone"/></module>
    <module name="LineLength"><property name="max" value="120"/></module>
  </module>
</module>
```

---
## 15. Workflow
| Step | Command |
|------|---------|
| Test | `mvn -q test -Dtest="!*Harness"` |
| Format | `mvn spotless:apply` |
| Lint | `mvn checkstyle:check` |
| Full | `xvfb-run -a mvn verify` |

---
## 16. References
- Venkat Subramaniam (2014). *Functional Programming in Java*. Pragmatic Bookshelf. [Publisher](https://pragprog.com/titles/vsjava8/functional-programming-in-java/)
- Joshua Bloch (2018). *Effective Java* (3rd Ed.). Addison-Wesley. [ISBN 978-0134685991](https://www.pearson.com/en-us/subject-catalog/p/effective-java/P200000005431/9780134685991)
- Robert C. Martin (2008). *Clean Code: A Handbook of Agile Software Craftsmanship*. Prentice Hall. [ISBN 978-0132350884](https://www.pearson.com/en-us/subject-catalog/p/clean-code/P200000000623/9780132350884)
- Brian Goetz et al. (2006). *Java Concurrency in Practice*. Addison-Wesley. [Site](https://jcip.net/)
- Brian Goetz & Java Language Architects. *Official blog / design notes* (lambdas, streams, language evolution). [Archive](https://mail.openjdk.org/mailman/listinfo/lambda-dev)

---
**Deviations**: Justify briefly in commit message.
