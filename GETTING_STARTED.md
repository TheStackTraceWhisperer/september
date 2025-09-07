# Getting Started

Audience: A developer who just cloned this repository and is sitting at the project root.
Goal: Get you exploring, building, running, rebranding, and extending the template fast.

---
## 1. Quick Project Tour

```
src/main/java/io/thestacktracewhisperer/september/
  Main.java          -> Entry point (creates GLFW + window) 
  GlfwContext.java   -> init/terminate GLFW safely
  WindowContext.java -> creates OpenGL 4.6 core profile window
pom.xml               -> Build config (Java 21, LWJGL, tests, coverage)
Dockerfile            -> Headless build/run via Xvfb
README.md             -> High-level overview
denied.md             -> Deferred/denied change log
```

Open these first:
- Main.java
- WindowContext.java
- pom.xml

---
## 2. Build & Test

Standard full build (unit + integration tests + coverage reports):
```
mvn clean verify
```
Artifacts & reports:
```
target/september-1.0-SNAPSHOT.jar
target/site/jacoco-unit/index.html         (unit coverage)
target/site/jacoco-it/index.html           (IT coverage)
target/site/jacoco-merged/index.html       (merged)
```
Skip tests (faster iteration):
```
mvn -DskipTests -DskipITs package
```

---
## 3. Run the Application Locally

Currently the jar is NOT executable (no Main-Class in manifest). Run with explicit main class:
```
java -cp target/september-1.0-SNAPSHOT.jar io.thestacktracewhisperer.september.Main
```
(Optionally add a Main-Class manifest later—see Section 8, Next Steps.)

---
## 4. Typical Edit Loop

1. Edit a class (e.g., Main.java).
2. Recompile quickly:
   ```
   mvn -q compile
   ```
3. Re-run:
   ```
   java -cp target/september-1.0-SNAPSHOT.jar io.thestacktracewhisperer.september.Main
   ```
4. Add / adjust tests, then:
   ```
   mvn test      # unit tests only
   mvn verify    # full lifecycle
   ```

---
## 5. Headless / Container Run

Docker build + run (uses Xvfb already):
```
docker build -t september-build .
docker run --rm september-build
```
Run locally in headless mode with xvfb-run (if you want):
```
xvfb-run -a java -cp target/september-1.0-SNAPSHOT.jar \
  io.thestacktracewhisperer.september.Main
```

---
## 6. Rebrand the Project

Goal: Rename package, groupId, artifactId, window title.

### 6.1 Choose New Coordinates
Suppose new groupId = `com.example.game` and artifactId = `nebula`.

### 6.2 Update pom.xml
Change:
```
<groupId>com.example.game</groupId>
<artifactId>nebula</artifactId>
```
(Optional) set a release version:
```
<version>0.1.0-SNAPSHOT</version>
```

### 6.3 Rename Package
From:
```
src/main/java/io/thestacktracewhisperer/september
```
To (create dirs then move files):
```
src/main/java/com/example/game/nebula
```
Then update the `package` declaration at the top of each moved file.

### 6.4 Fix Imports & References
Search & replace:
- `io.thestacktracewhisperer.september` -> `com.example.game.nebula`
- Update README / docs text occurrences of "September" if desired.

### 6.5 Window / Branding Strings
In `Main.java` and `WindowContext.java` change the window title (currently `"LWJGL Window"`).

### 6.6 Clean & Rebuild
```
mvn clean verify
```
If something fails: ensure directory structure matches package name exactly.

### 6.7 (Optional) Add Executable Jar Manifest
Add to `maven-jar-plugin` config:
```
<manifest>
  <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
  <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
  <mainClass>com.example.game.nebula.Main</mainClass>
</manifest>
```
Then run:
```
java -jar target/nebula-0.1.0-SNAPSHOT.jar
```

---
## 7. Add a Game Loop Skeleton

Create a simple loop (replace sleep demo):
```java
boolean running = true;
long start = System.nanoTime();
while (running && !GLFW.glfwWindowShouldClose(window.handle())) {
  // poll events
  GLFW.glfwPollEvents();
  // update (stub)
  // render (stub)
  GLFW.glfwSwapBuffers(window.handle());
}
```
Add FPS tracking later if needed.

---
## 8. Coverage & Reports

Commands:
```
mvn verify
```
Outputs (HTML):
```
target/site/jacoco-unit
target/site/jacoco-it
target/site/jacoco-merged
```
Typical usage: keep an eye on merged coverage while refactoring core.

---
## 9. Common Extensions (Next Steps)

| Area | Idea |
|------|------|
| Logging | Swap slf4j-simple for logback-classic profile. |
| Packaging | Add shade plugin for a fat jar. |
| Native Distribution | Bundle assets + create a platform script/launcher. |
| CI | Add GitHub Action to publish coverage badge & release artifacts. |
| Input | Add keyboard/mouse event handling with GLFW callbacks. |
| Rendering | Introduce VAOs/VBOs & a shader loader class. |
| Config | Externalize window size/title via a JSON or properties file. |

---
## 10. Troubleshooting Quick Hits

| Symptom | Fix |
|---------|-----|
| `UnsatisfiedLinkError` for GLFW | Ensure natives profile activated (OS auto) and clean build. |
| Black window / no render | Confirm context current + GL version logged. |
| Coverage report empty | No unit tests executed; add one under `src/test/java`. |
| Docker slow render | Software rasterizer (llvmpipe); expected without host GPU pass-through. |

---
## 11. Minimal First Unit Test Example

Create `src/test/java/.../GlfwContextTest.java`:
```java
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;public class GlfwContextTest { @Test void openAndClose() { assertDoesNotThrow(() -> { try (var ctx = GlfwContext.open()) {} }); }}
```
Run:
```
mvn test
```

---
## 12. You Are Ready
Cut code, iterate fast, and remove template parts you don’t need.

