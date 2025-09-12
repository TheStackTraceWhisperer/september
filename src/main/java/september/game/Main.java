package september.game;

import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;
import september.engine.assets.ResourceManager;
import september.engine.core.Engine;
import september.engine.core.MainLoopPolicy;
import september.engine.core.SystemTimer;
import september.engine.core.TimeService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.input.InputService;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ecs.World;
import september.engine.ecs.components.ControllableComponent;
import september.engine.ecs.components.MovementStatsComponent;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.rendering.Camera;
import september.engine.systems.MovementSystem;
import september.game.components.EnemyComponent;
import september.game.components.PlayerComponent;
import september.game.input.InputMappingService;
import september.game.input.KeyboardMappingService;
import september.game.systems.EnemyAISystem;
import september.game.systems.PlayerInputSystem;

/**
 * The entry point for the game application.
 *
 * This class is the "wiring root" or "composition root". Its only responsibility
 * is to instantiate all the necessary services, configure the initial game state
 * (entities and components), and then create and run the main Engine instance.
 */
@Slf4j
public final class Main implements Runnable {
  private final MainLoopPolicy loopPolicy;

  public Main() {
    this(MainLoopPolicy.standard());
  }

  public Main(MainLoopPolicy loopPolicy) {
    this.loopPolicy = loopPolicy;
  }

  @Override
  public void run() {
    // --- 1. Create Context-Independent Services and Configuration ---
    TimeService timeService = new SystemTimer();
    IWorld world = new World();
    ResourceManager resourceManager = new ResourceManager();
    InputService inputService = new GlfwInputService();
    InputMappingService mappingService = new KeyboardMappingService(inputService);

    final float VIRTUAL_WIDTH = 800.0f;
    final float VIRTUAL_HEIGHT = 600.0f;
    Camera camera = new Camera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);


    // --- CAMERA CONFIGURATION ---
    // Move the camera 5 units "out of the screen" to look at the origin.
    camera.setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // Set up the perspective projection.
    // This defines a 45-degree field of view, an aspect ratio for an 800x600 window,
    // and a view distance from 0.1 to 100 units.
    camera.setPerspective(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);


    // --- 2. Load Game Assets ---
    // NOTE: This call has been MOVED into the Engine class.
    // We cannot load textures here because there is no OpenGL context yet.
    // resourceManager.loadTexture("player_texture", "textures/player.png");

    // --- 3. Define Initial Game State ---
    int playerEntity = world.createEntity();
    world.addComponent(playerEntity, new TransformComponent());
    // We still define WHAT sprite to use, but the loading happens later.
    world.addComponent(playerEntity, new SpriteComponent("player_texture"));
    world.addComponent(playerEntity, new ControllableComponent());
    world.addComponent(playerEntity, new MovementStatsComponent(2.5f));
    world.addComponent(playerEntity, new PlayerComponent()); // Tag the entity as the player

    int enemyEntity = world.createEntity();
    world.addComponent(enemyEntity, new TransformComponent());
    world.addComponent(enemyEntity, new SpriteComponent("enemy_texture"));
    world.addComponent(enemyEntity, new MovementStatsComponent(2.5f));
    world.addComponent(enemyEntity, new EnemyComponent());

    // --- 4. Create Systems ---
    // The order is important: input should be processed before movement.
    ISystem playerInputSystem = new PlayerInputSystem(world, mappingService);
    ISystem movementSystem = new MovementSystem(world);
    ISystem enemyAISystem = new EnemyAISystem(world, timeService);

    // --- 5. Create and Run the Engine ---
    Engine gameEngine = new Engine(world, timeService, resourceManager, camera, inputService, loopPolicy, playerInputSystem, movementSystem, enemyAISystem);

    try {
      gameEngine.run();
    } catch (Exception e) {
      log.error("A fatal error occurred in the engine.", e);
    }
  }

  public static void main(String[] args) {
    new Main().run();
  }
}
