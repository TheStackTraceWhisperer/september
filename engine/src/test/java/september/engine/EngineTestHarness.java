package september.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.Engine;
import september.engine.core.EngineServices;
import september.engine.core.Game;
import september.engine.core.MainLoopPolicy;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.rendering.Camera;
import september.engine.state.GameState;

/**
 * A JUnit 5 test harness that bootstraps a live game engine with a valid OpenGL context and audio system before each test.
 * <p>
 * Tests can extend this class to safely work with OpenGL-dependent components like Shaders, Textures,
 * or audio-dependent components like AudioBuffer and AudioSource, without needing to run the full game loop.
 * It provides direct access to the core engine components like the {@link IWorld} and {@link ResourceManager}.
 */
public abstract class EngineTestHarness {

  protected Engine engine;
  protected IWorld world;
  protected ResourceManager resourceManager;
  protected Camera camera;
  protected AudioManager audioManager;
  protected SystemManager systemManager;

  /**
   * A minimal GameState implementation for the test harness.
   * Its only job is to load the essential assets that integration tests rely on.
   */
  private static class TestGameState implements GameState {
    @Override
    public void onEnter(EngineServices services) {
      // Load essential engine assets that systems rely on.
      float[] vertices = {
        0.5f, 0.5f, 0.0f, 1.0f, 1.0f,
        0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
        -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, 0.0f, 0.0f, 1.0f
      };
      int[] indices = {0, 1, 3, 1, 2, 3};
      services.resourceManager().loadProceduralMesh("quad", vertices, indices);
      services.resourceManager().loadTexture("player_texture", "textures/player.png");
      services.resourceManager().loadTexture("enemy_texture", "textures/enemy.png");
    }

    @Override
    public void onUpdate(EngineServices services, float deltaTime) {
      // No-op for the harness.
    }

    @Override
    public void onExit() {
      // No-op for the harness.
    }
  }

  /**
   * A minimal Game implementation that provides the TestGameState as the entry point.
   */
  private static class TestGame implements Game {
    @Override
    public GameState getInitialState(EngineServices services) {
      return new TestGameState();
    }
  }

  @BeforeEach
  void setUp() {
    Game testGame = new TestGame();
    engine = new Engine(testGame, MainLoopPolicy.skip());
    engine.init();

    // Retrieve all the live, initialized services from the engine instance.
    world = engine.getWorld();
    resourceManager = engine.getResourceManager();
    camera = engine.getCamera();
    audioManager = engine.getAudioManager();
    systemManager = engine.getSystemManager();
  }

  @AfterEach
  void tearDown() {
    if (engine != null) {
      engine.shutdown();
    }
  }
}
