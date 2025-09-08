package september.game;

import september.engine.assets.ResourceManager;
import september.engine.core.Engine;
import september.engine.core.MainLoopPolicy;
import september.engine.core.SystemTimer;
import september.engine.core.TimeService;
import september.engine.ecs.IWorld;
import september.engine.ecs.World;
import september.engine.ecs.components.MeshComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.rendering.Camera;

/**
 * The entry point for the game application.
 * <p>
 * This class is the "wiring root" or "composition root". Its only responsibility
 * is to instantiate all the necessary services, configure the initial game state
 * (entities and components), and then create and run the main Engine instance.
 */
public final class Main implements Runnable {

  private final MainLoopPolicy loopPolicy;

  /**
   * Creates a new Main instance with the standard loop policy (runs until window close).
   */
  public Main() {
    this(MainLoopPolicy.standard());
  }

  /**
   * Creates a new Main instance with a specific loop continuation policy.
   * @param loopPolicy The policy that determines when the main loop should terminate.
   */
  public Main(MainLoopPolicy loopPolicy) {
    this.loopPolicy = loopPolicy;
  }

  /**
   * Wires together the application's services and runs the engine.
   */
  @Override
  public void run() {
    // --- 1. Create Context-Independent Services and Configuration ---
    TimeService timeService = new SystemTimer();
    IWorld world = new World();
    ResourceManager resourceManager = new ResourceManager();
    Camera camera = new Camera();

    // --- 2. Define Initial Game State ---
    int quadEntity = world.createEntity();
    world.addComponent(quadEntity, new TransformComponent());
    world.addComponent(quadEntity, new MeshComponent("quad"));

    // --- 3. Create and Run the Engine ---
    // The Engine is instantiated with the loopPolicy provided to this Main instance.
    Engine gameEngine = new Engine(world, timeService, resourceManager, camera, this.loopPolicy);

    try {
      gameEngine.run();
    } catch (Exception e) {
      System.err.println("A fatal error occurred. The application will now exit.");
      e.printStackTrace();
    }
  }

  /**
   * The application's main entry point.
   * Creates an instance of Main and executes its run method.
   * @param args command line arguments (unused).
   */
  public static void main(String[] args) {
    // To use the default behavior: new Main().run();
    // To use an override as per your example: new Main(MainLoopPolicy.frames(0)).run();
    new Main().run();
  }
}

