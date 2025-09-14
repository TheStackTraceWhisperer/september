# Environment Configuration

## XKB Warning Suppression

To minimize verbose XKB keyboard warnings during GLFW initialization in headless environments, set these environment variables **before** running the application:

```bash
export XKB_LOG_LEVEL=0
export XKB_LOG_VERBOSITY=0
export LC_ALL=C
```

For headless X11 sessions (e.g., Xvfb), also set:
```bash
export XMODIFIERS=""
export QT_QPA_PLATFORM=xcb
```

## Recommended Execution

Use the provided script that configures the environment properly:

```bash
./run-september.sh
```

This approach addresses the root cause of XKB warnings by configuring the X11 environment before GLFW initialization, rather than suppressing output after the fact.

## Alternative: Docker/CI Configuration

For CI systems or Docker containers, these environment variables can be set at the container or build level to avoid needing Java-level configuration.