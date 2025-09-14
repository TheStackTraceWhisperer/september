# XKB Warning Suppression

## The Problem
When running September on Linux systems with X11, you may see verbose XKB (X Keyboard Extension) warnings during GLFW initialization, such as:

```
Warning: Could not resolve keysym XF86CameraAccessEnable
Warning: Could not resolve keysym XF86CameraAccessDisable
Warning: Could not resolve keysym XF86CameraAccessToggle
... (hundreds more similar warnings)
```

These warnings are harmless but create noise in the console output. They occur when the X11 keyboard compiler (xkbcomp) encounters unknown keysyms during keyboard map compilation.

## The Solution

### Automatic Suppression (Default)
By default, September automatically suppresses these warnings during GLFW initialization. This is controlled by the system property `september.suppress.xkb.warnings` which defaults to `true`.

### Manual Control
You can control this behavior:

**To disable suppression (show all warnings):**
```bash
java -Dseptember.suppress.xkb.warnings=false ...
```

**To enable suppression explicitly:**
```bash
java -Dseptember.suppress.xkb.warnings=true ...
```

### Alternative: Shell Script
For the cleanest output, use the provided `run-quiet.sh` script which sets environment variables before starting the JVM:

```bash
./run-quiet.sh
```

This script sets `XKB_LOG_LEVEL=0` and other environment variables to minimize X11 verbosity at the system level.

## Technical Details
The suppression works by temporarily redirecting stderr during GLFW context and window creation, which are the operations that trigger XKB keyboard map compilation. This approach preserves all other error output while suppressing only the noisy XKB warnings.