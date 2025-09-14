package september.game;

import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;
import september.engine.assets.ResourceManager;
import september.engine.core.Engine;
import september.engine.core.EngineServices;
import september.engine.core.Game;
import september.engine.core.MainLoopPolicy;
import september.engine.core.SystemTimer;
import september.engine.core.TimeService;
import september.engine.core.input.GamepadService;
import september.engine.core.input.GlfwGamepadService;
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
import september.game.input.MultiDeviceMappingService;
import september.game.systems.EnemyAISystem;
import september.game.systems.PlayerInputSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The entry point for the game application.
 *
 * This class is the "wiring root" or "composition root". Its only responsibility
 * is to instantiate all the necessary services, configure the initial game state
 * (entities and components), and then create and run the main Engine instance.
 */
@Slf4j
public final class Main implements Game {
  // Suppress noisy XKB warnings at class loading time
  static {
    // These system properties help reduce X11/XKB verbosity during GLFW initialization
    System.setProperty("java.awt.headless", "true");
    System.setProperty("org.lwjgl.system.stackSize", "128");
  }

  private final MainLoopPolicy loopPolicy;
  private final List<ISystem> gameSystems = new ArrayList<>();

  public Main() {
    this(MainLoopPolicy.standard());
  }

  public Main(MainLoopPolicy loopPolicy) {
    this.loopPolicy = loopPolicy;
  }

  @Override
  public void init(EngineServices services) {
    // --- 1. Load Game Assets ---
    services.resourceManager().loadTexture("player_texture", "textures/player.png");
    services.resourceManager().loadTexture("enemy_texture", "textures/enemy.png");
    float[] vertices = { 0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f };
    int[] indices = {0, 1, 3, 1, 2, 3};
    services.resourceManager().loadProceduralMesh("quad", vertices, indices);


    // --- 2. Configure Engine Services (e.g., Camera) ---
    services.camera().setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // --- 3. Define Initial Game State ---
    var world = services.world();
    int playerEntity = world.createEntity();
    world.addComponent(playerEntity, new TransformComponent());
    world.addComponent(playerEntity, new SpriteComponent("player_texture"));
    world.addComponent(playerEntity, new ControllableComponent());
    world.addComponent(playerEntity, new MovementStatsComponent(2.5f));
    world.addComponent(playerEntity, new PlayerComponent());

    int enemyEntity = world.createEntity();
    world.addComponent(enemyEntity, new TransformComponent());
    world.addComponent(enemyEntity, new SpriteComponent("enemy_texture"));
    world.addComponent(enemyEntity, new MovementStatsComponent(2.5f));
    world.addComponent(enemyEntity, new EnemyComponent());

    // --- 4. Create and Store Game Systems ---
    InputMappingService mappingService = new MultiDeviceMappingService(services.inputService(), services.gamepadService());
    gameSystems.add(new PlayerInputSystem(world, mappingService));
    gameSystems.add(new MovementSystem(world));
    gameSystems.add(new EnemyAISystem(world, services.timeService()));
  }

  @Override
  public Collection<ISystem> getSystems() {
    return gameSystems;
  }

  @Override
  public void shutdown() {
    // No game-specific shutdown logic is needed for this simple game.
    log.info("Game shutting down.");
  }

  public static void main(String[] args) {
    // The application entry point.
    try {
      Game myGame = new Main();
      Engine gameEngine = new Engine(myGame, MainLoopPolicy.standard());
      gameEngine.run();
    } catch (Exception e) {
      log.error("A fatal error occurred.", e);
    }
  }
}
