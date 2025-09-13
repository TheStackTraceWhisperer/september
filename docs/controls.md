# September Game Controls

This document describes the default control mappings and gamepad support in the September game engine.

## Overview

The September game engine supports both keyboard and gamepad input with automatic device detection and fallback mechanisms. Up to 8 controllers (gamepads) can be used simultaneously for multiplayer gameplay.

## Default Control Mappings

### Player 0 (Primary Player)

**Keyboard Controls (Fallback):**
- **Movement**: WASD keys
  - W: Move Up
  - S: Move Down
  - A: Move Left  
  - D: Move Right
- **Actions**:
  - Space: Attack

**Gamepad Controls (Preferred):**
- **Movement**: Left analog stick
  - Up/Down: Left stick Y-axis (deadzone: 0.25)
  - Left/Right: Left stick X-axis (deadzone: 0.25)
- **Actions**:
  - A button: Attack

### Players 1-7 (Additional Players)

**Gamepad Controls Only:**
- Same gamepad mapping as Player 0
- Automatically assigned to gamepads 1-7 when connected

## Device Assignment

The input system uses the following priority for device assignment:

1. **Auto-Assignment**: Connected gamepads are automatically assigned to players 0-7 in order
2. **Player 0 Fallback**: If no gamepad is connected for Player 0, keyboard controls are automatically used
3. **Manual Override**: Players can be manually bound to specific devices using the programming API

## Gamepad Support

### Compatible Controllers
- Any controller recognized by GLFW as a standard gamepad
- Support for up to 8 simultaneous controllers
- Uses GLFW's standard gamepad mapping

### Technical Details
- **Deadzone**: 0.25 (25% of stick range) to prevent drift
- **Polling**: Gamepad state is polled each frame via GLFW
- **Connection Detection**: Automatic detection of gamepad connect/disconnect events

## Programming API

### Manual Device Binding

```java
MultiDeviceMappingService mappingService = // ... obtain service
// Bind player 0 to keyboard
mappingService.bindPlayerToKeyboard(0);

// Bind player 1 to gamepad 0
mappingService.bindPlayerToGamepad(1, 0);

// Refresh automatic assignments
mappingService.refreshAssignments();

// Get maximum supported players
int maxPlayers = mappingService.getMaxSupportedPlayers(); // Returns 8
```

### Configuration

The default mappings are hard-coded but the system is designed to support data-driven configuration in the future. The `MultiDeviceMappingService` can be extended or replaced to support custom control schemes.

## Troubleshooting

### Gamepad Not Detected
1. Ensure the controller is properly connected
2. Verify the controller is recognized by your operating system
3. Check that the controller supports standard gamepad mapping
4. Try disconnecting and reconnecting the controller

### Input Lag or Responsiveness
1. Adjust the deadzone if stick drift is an issue
2. Ensure the game is running at stable frame rates
3. Check for driver updates for your controller

### Multiple Players
1. Connect controllers before starting the game for best results
2. Use `refreshAssignments()` if controllers are connected during gameplay
3. Remember that Player 0 will fall back to keyboard if no gamepad is available

## Future Enhancements

- Data-driven control configuration via JSON/XML files
- In-game control remapping interface
- Support for additional input devices (mouse, specialized controllers)
- Per-player control scheme customization
- Analog movement support (currently uses digital booleans)