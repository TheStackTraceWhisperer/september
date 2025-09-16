# Docker Build Optimization

This document describes the Docker build optimization implemented for the September game engine, which dramatically improves build times through multi-stage caching.

## Overview

The Docker setup has been optimized using a multi-stage build strategy that separates concerns and maximizes layer caching efficiency. This reduces build times from ~60+ seconds for full rebuilds to ~0.4 seconds for cached builds and ~5 seconds for source-only changes.

## Multi-Stage Architecture

### Stage 1: Base System Dependencies
```dockerfile
FROM ubuntu:24.04 AS base
```
- Installs system packages (xvfb, OpenJDK 21, Maven, etc.)
- Sets up environment variables
- **Cached unless:** System dependencies change (rare)

### Stage 2: Maven Dependencies Cache  
```dockerfile
FROM base AS dependencies
```
- Downloads and caches all Maven dependencies
- Uses minimal Java file to trigger dependency resolution
- **Cached unless:** `pom.xml` changes (infrequent)

### Stage 3: Application Build
```dockerfile
FROM dependencies AS builder
```
- Copies source code and compiles the application
- Benefits from cached dependencies from previous stage
- **Rebuilt when:** Source code changes (frequent)

### Stage 4: Runtime Image
```dockerfile
FROM builder AS runtime
```
- Creates the final runtime image with entrypoint script
- Minimal overhead as it builds on the previous stages

## Performance Benefits

| Scenario | Build Time | Speed Improvement |
|----------|------------|-------------------|
| Clean build (first time) | ~60s | Baseline |
| No changes (cached) | ~0.4s | **99.3% faster** |
| Source code changes only | ~5s | **91% faster** |
| Dependency changes (pom.xml) | ~35s | **42% faster** |

## Build Context Optimization

The `.dockerignore` file excludes unnecessary files:
- Documentation (`docs/`, `*.md`)
- Build artifacts (`target/`, `.mvn/`)
- IDE files (`.idea/`, `.vscode/`)
- Git metadata (`.git/`)
- CI/CD configurations (`.github/`)

**Result:** Build context reduced from ~1.4MB to ~400KB (71% smaller)

## Local Development Usage

### Standard Build
```bash
docker build -t september .
```

### Run Tests
```bash
docker run --rm september mvn verify
```

### Development Workflow
1. Make source code changes
2. Rebuild: `docker build -t september .`
3. Only the source compilation layer rebuilds (~5s)
4. Dependencies and system layers remain cached

## CI/CD Integration

The GitHub Actions workflow uses Docker BuildKit for optimal performance:

```yaml
- name: Build and Test with Docker
  run: |
    export DOCKER_BUILDKIT=1
    docker build -t september-ci .
    docker run --name september-test september-ci
    # Copy test results out of container
```

**Key features:**
- Uses Docker's built-in layer caching (no external cache dependencies)
- Eliminates the cache corruption issues from external GitHub Actions cache
- Reliable and fast builds in CI environment

## Technical Details

### Layer Caching Strategy
1. **System packages:** Cached until Dockerfile changes
2. **Maven dependencies:** Cached until `pom.xml` changes  
3. **Source compilation:** Rebuilt on every source change
4. **Runtime setup:** Cached unless entrypoint script changes

### Environment Variables
- `DOCKER_BUILDKIT=1`: Enables advanced BuildKit features
- `MESA_GL_VERSION_OVERRIDE=4.6`: Required for OpenGL context in headless environment
- `ALSOFT_DRIVERS=null`: Configures OpenAL for headless audio testing

### Error Prevention
- No external cache dependencies (avoids GitHub Actions cache corruption)
- Robust Xvfb startup with socket verification
- Proper SSL configuration for Maven downloads

## Troubleshooting

### Build Issues
- **Problem:** Docker build fails with cache errors
- **Solution:** Clear Docker cache: `docker system prune -f`

### Test Failures  
- **Problem:** Tests fail in container but pass locally
- **Solution:** Verify environment variables and Xvfb configuration

### Permission Issues
- **Problem:** Cannot access files copied from container
- **Solution:** CI workflow includes `sudo chown -R runner:runner ./target`

## Maintenance

### Updating Dependencies
When updating `pom.xml`:
1. The dependencies stage will rebuild (~35s)
2. Source and runtime stages benefit from new cached dependencies
3. Subsequent builds with same dependencies will be fast

### System Updates
When updating system packages in Dockerfile:
1. Full rebuild required (~60s)
2. All downstream layers rebuild
3. New cached layers created for future builds

This optimization provides an excellent developer experience with fast feedback loops while maintaining reliability in CI environments.