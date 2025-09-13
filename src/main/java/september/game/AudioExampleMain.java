//package september.game;
//
//import lombok.extern.slf4j.Slf4j;
//import org.joml.Vector3f;
//import september.engine.assets.ResourceManager;
//import september.engine.audio.AudioManager;
//import september.engine.core.Engine;
//import september.engine.core.MainLoopPolicy;
//import september.engine.core.SystemTimer;
//import september.engine.core.TimeService;
//import september.engine.core.input.GamepadService;
//import september.engine.core.input.GlfwGamepadService;
//import september.engine.core.input.GlfwInputService;
//import september.engine.core.input.InputService;
//import september.engine.ecs.ISystem;
//import september.engine.ecs.IWorld;
//import september.engine.ecs.World;
//import september.engine.ecs.components.AudioSourceComponent;
//import september.engine.ecs.components.ControllableComponent;
//import september.engine.ecs.components.MovementStatsComponent;
//import september.engine.ecs.components.MusicComponent;
//import september.engine.ecs.components.SoundEffectComponent;
//import september.engine.ecs.components.SpriteComponent;
//import september.engine.ecs.components.TransformComponent;
//import september.engine.rendering.Camera;
//import september.engine.systems.AudioSystem;
//import september.engine.systems.MovementSystem;
//import september.game.components.EnemyComponent;
//import september.game.components.GameSoundEffectType;
//import september.game.components.PlayerComponent;
//import september.game.input.InputMappingService;
//import september.game.input.MultiDeviceMappingService;
//import september.game.systems.EnemyAISystem;
//import september.game.systems.PlayerInputSystem;
//
///**
// * Example implementation showing how to integrate the audio system into the September engine.
// * <p>
// * This demonstrates the complete setup including:
// * - Audio manager initialization
// * - Audio resource loading
// * - Background music with fade effects
// * - Positioned 3D audio sources
// * - UI sound effects
// * - System registration and cleanup
// * <p>
// * NOTE: This is an example class for documentation purposes.
// * It shows how audio would be integrated but is not the actual Main class.
// */
//@Slf4j
//public final class AudioExampleMain implements Runnable {
//  private final MainLoopPolicy loopPolicy;
//
//  public AudioExampleMain() {
//    this(MainLoopPolicy.standard());
//  }
//
//  public AudioExampleMain(MainLoopPolicy loopPolicy) {
//    this.loopPolicy = loopPolicy;
//  }
//
//  @Override
//  public void run() {
//    // --- 1. Create Context-Independent Services and Configuration ---
//    TimeService timeService = new SystemTimer();
//    IWorld world = new World();
//    ResourceManager resourceManager = new ResourceManager();
//    InputService inputService = new GlfwInputService();
//    GamepadService gamepadService = new GlfwGamepadService();
//    InputMappingService mappingService = new MultiDeviceMappingService(inputService, gamepadService);
//
//    // NEW: Create audio manager
//    AudioManager audioManager = new AudioManager();
//
//    final float VIRTUAL_WIDTH = 800.0f;
//    final float VIRTUAL_HEIGHT = 600.0f;
//    Camera camera = new Camera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
//
//    // --- CAMERA CONFIGURATION ---
//    camera.setPosition(new Vector3f(0.0f, 0.0f, 5.0f));
//    camera.setPerspective(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);
//
//    // --- 2. Load Game Assets (including audio) ---
//    // NOTE: Audio assets would be loaded after OpenAL initialization in the Engine
//
//    // --- 3. Define Initial Game State with Audio ---
//
//    // Create background music entity
//    int musicEntity = world.createEntity();
//    MusicComponent backgroundMusic = new MusicComponent("background-music.ogg", 0.6f, true, 3.0f);
//    world.addComponent(musicEntity, backgroundMusic);
//
//    // Create player entity with movement sounds
//    int playerEntity = world.createEntity();
//    world.addComponent(playerEntity, new TransformComponent());
//    world.addComponent(playerEntity, new SpriteComponent("player_texture"));
//    world.addComponent(playerEntity, new ControllableComponent());
//    world.addComponent(playerEntity, new MovementStatsComponent(2.5f));
//    world.addComponent(playerEntity, new PlayerComponent());
//
//    // Add footstep audio to player (positioned 3D audio)
//    AudioSourceComponent footstepAudio = new AudioSourceComponent("footstep.ogg", 0.4f, false, false);
//    world.addComponent(playerEntity, footstepAudio);
//
//    // Create enemy entity with ambient sound
//    int enemyEntity = world.createEntity();
//    TransformComponent enemyTransform = new TransformComponent();
//    enemyTransform.position.set(5.0f, 0.0f, 0.0f); // Position enemy to the right
//    world.addComponent(enemyEntity, enemyTransform);
//    world.addComponent(enemyEntity, new SpriteComponent("enemy_texture"));
//    world.addComponent(enemyEntity, new MovementStatsComponent(2.5f));
//    world.addComponent(enemyEntity, new EnemyComponent());
//
//    // Add ambient enemy sound (3D positioned, looping)
//    AudioSourceComponent enemyAmbient = new AudioSourceComponent("enemy-growl.ogg", 0.3f, true, true);
//    world.addComponent(enemyEntity, enemyAmbient);
//
//    // Create UI entity for menu sounds
//    int uiEntity = world.createEntity();
//    // UI sounds are typically triggered programmatically, so we don't add them here
//
//    // --- 4. Create Systems ---
//    ISystem playerInputSystem = new PlayerInputSystem(world, mappingService);
//    ISystem movementSystem = new MovementSystem(world);
//    ISystem enemyAISystem = new EnemyAISystem(world, timeService);
//
//    // NEW: Create audio system
//    AudioSystem audioSystem = new AudioSystem(world, audioManager, resourceManager);
//
//    // --- 5. Create and Run the Engine ---
//    Engine gameEngine = new Engine(
//        world,
//        timeService,
//        resourceManager,
//        camera,
//        inputService,
//        loopPolicy,
//        playerInputSystem,
//        movementSystem,
//        enemyAISystem,
//        audioSystem  // Add audio system to the engine
//    );
//
//    try {
//      gameEngine.run();
//    } catch (Exception e) {
//      log.error("Exception occurred while running the game engine", e);
//    } finally {
//      // Ensure audio resources are cleaned up
//      audioManager.close();
//    }
//  }
//
//  /**
//   * Example method showing how to trigger sound effects in response to game events.
//   * This would typically be called from within a system when certain events occur.
//   */
//  public void triggerGameSounds(AudioSystem audioSystem, IWorld world) {
//    // Example: Player picks up an item
//    int playerEntity = getPlayerEntity(world);
//    audioSystem.playSoundEffect(playerEntity, "item-pickup.ogg", GameSoundEffectType.ITEM_PICKUP, 0.8f);
//
//    // Example: UI button click
//    int uiEntity = world.createEntity();
//    SoundEffectComponent clickSound = new SoundEffectComponent("ui-click.ogg", GameSoundEffectType.UI_BUTTON_CLICK, 1.0f);
//    world.addComponent(uiEntity, clickSound);
//
//    // Example: Player takes damage with fade-out of background music
//    audioSystem.fadeOutAllMusic();
//    audioSystem.playSoundEffect(playerEntity, "player-damage.ogg", GameSoundEffectType.PLAYER_DAMAGE, 0.9f);
//  }
//
//  private int getPlayerEntity(IWorld world) {
//    // This would typically be implemented to find the player entity
//    // For this example, we'll return a dummy value
//    return 1;
//  }
//
//  /**
//   * Example of how to load audio resources after OpenAL initialization.
//   * This would typically be called from within the Engine after context creation.
//   */
//  public void loadAudioResources(ResourceManager resourceManager) {
//    // Load music
//    resourceManager.loadAudioBuffer("background-music", "/audio/background-music.ogg");
//    resourceManager.loadAudioBuffer("menu-music", "/audio/menu-music.ogg");
//
//    // Load sound effects
//    resourceManager.loadAudioBuffer("ui-click", "/audio/ui-click.ogg");
//    resourceManager.loadAudioBuffer("ui-hover", "/audio/ui-hover.ogg");
//    resourceManager.loadAudioBuffer("item-pickup", "/audio/item-pickup.ogg");
//    resourceManager.loadAudioBuffer("footstep", "/audio/footstep.ogg");
//    resourceManager.loadAudioBuffer("player-damage", "/audio/player-damage.ogg");
//
//    // Load ambient sounds
//    resourceManager.loadAudioBuffer("enemy-growl", "/audio/enemy-growl.ogg");
//    resourceManager.loadAudioBuffer("wind-ambient", "/audio/wind-ambient.ogg");
//  }
//
//  public static void main(String[] args) {
//    new AudioExampleMain().run();
//  }
//}
