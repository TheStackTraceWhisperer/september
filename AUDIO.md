# Audio System Documentation

The September engine now includes a comprehensive audio system built on OpenAL, following the same ECS patterns as the rest of the engine.

## Core Components

### Audio Infrastructure

- **`AudioManager`**: Manages the OpenAL context and provides high-level audio services
- **`AudioBuffer`**: Wrapper for OpenAL audio buffers with OGG Vorbis loading support  
- **`AudioSource`**: Wrapper for OpenAL audio sources with 3D positioning and playback control

### Audio Components

- **`AudioSourceComponent`**: For entities that play positioned 3D audio
- **`MusicComponent`**: For background music with looping and fade effects
- **`SoundEffectComponent`**: For one-shot UI and 2D sound effects

### Audio System

- **`AudioSystem`**: ECS system that manages all audio playback, fade effects, and cleanup

## Usage Examples

### Playing Background Music

```java
// Create an entity for background music
int musicEntity = world.createEntity();

// Add music component with fade effects
MusicComponent music = new MusicComponent("background-music.ogg", 0.8f, true, 2.0f);
world.addComponent(musicEntity, music);

// Music will automatically start playing with fade-in when AudioSystem updates
```

### Playing Sound Effects

```java
// Method 1: Add component directly
int soundEntity = world.createEntity();
SoundEffectComponent sound = new SoundEffectComponent("click.ogg", GameSoundEffectType.UI_BUTTON_CLICK);
world.addComponent(soundEntity, sound);

// Method 2: Use AudioSystem convenience method
audioSystem.playSoundEffect(entity, "click.ogg", GameSoundEffectType.UI_BUTTON_CLICK, 1.0f);
```

### Positioned 3D Audio

```java
// Create entity with transform and audio
int entityId = world.createEntity();

TransformComponent transform = new TransformComponent();
transform.position.set(10.0f, 0.0f, 5.0f);

AudioSourceComponent audio = new AudioSourceComponent("ambient.ogg", 0.6f, true, true);

world.addComponent(entityId, transform);
world.addComponent(entityId, audio);

// Audio will play at the entity's 3D position
```

### Music Control

```java
// Fade out all music
audioSystem.fadeOutAllMusic();

// Pause all music
audioSystem.pauseAllMusic();

// Resume all music  
audioSystem.resumeAllMusic();

// Individual music control
musicComponent.startFadeOut();
musicComponent.startFadeIn();
musicComponent.stopFade();
```

## Integration Setup

### Resource Loading

```java
// In your initialization code
resourceManager.loadAudioBuffer("music", "/audio/background.ogg");
resourceManager.loadAudioBuffer("click", "/audio/ui-click.ogg");
resourceManager.loadAudioBuffer("footstep", "/audio/footstep.ogg");
```

### System Registration

```java
// Create and register the audio system
AudioManager audioManager = new AudioManager();
audioManager.initialize();

AudioSystem audioSystem = new AudioSystem(world, audioManager, resourceManager);
world.registerSystem(audioSystem);
```

### Cleanup

```java
// In shutdown code
audioSystem.stopAll();
audioManager.close();
```

## Sound Effect Types

The engine provides a flexible type system for categorizing sound effects:

```java
public enum GameSoundEffectType implements SoundEffectComponent.SoundEffectType {
    // UI sounds
    UI_BUTTON_CLICK,
    UI_BUTTON_HOVER,
    UI_MENU_OPEN,
    
    // Player actions  
    PLAYER_JUMP,
    PLAYER_FOOTSTEP,
    
    // Combat sounds
    WEAPON_SWING,
    PROJECTILE_FIRE,
    
    // Environment
    ITEM_PICKUP,
    DOOR_OPEN
}
```

## Features

### Music System
- ✅ Looping background music
- ✅ Fade in/out effects with configurable duration
- ✅ Volume control and pitch adjustment
- ✅ Pause/resume functionality
- ✅ Global music control (pause all, fade all, etc.)

### Sound Effects
- ✅ One-shot playback with automatic cleanup
- ✅ Volume and pitch control
- ✅ Type-based categorization
- ✅ Programmatic triggering

### 3D Audio
- ✅ Positioned audio sources
- ✅ Automatic position updates from TransformComponent
- ✅ Listener position and orientation control

### Resource Management
- ✅ OGG Vorbis file loading via STB
- ✅ Audio buffer caching and sharing
- ✅ Automatic resource cleanup
- ✅ Integration with existing ResourceManager

## File Format Support

Currently supports OGG Vorbis (.ogg) files. The audio system uses STB Vorbis for decoding, which is already included with LWJGL.

## Performance Notes

- Audio buffers are cached and shared between multiple sources
- One-shot sound effects are automatically cleaned up after playback
- The system efficiently manages OpenAL sources and cleans up unused resources
- Fade effects are computed per-frame for smooth transitions