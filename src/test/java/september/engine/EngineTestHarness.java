package september.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.Engine;
import september.engine.core.EngineServices;
import september.engine.core.Game;
import september.engine.core.MainLoopPolicy;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;

import java.util.Collection;
import java.util.Collections;

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

  /**
   * A minimal, do-nothing implementation of the Game interface for testing purposes.
   * It allows the engine to initialize without needing a full game implementation.
   */
  private static class TestGame implements Game {
    @Override
    public void init(EngineServices services) {
      // Load essential engine assets that systems rely on.
      // This mirrors the asset loading that would happen in a real Game's init.
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
    public Collection<ISystem> getSystems() {
      return Collections.emptyList();
    }

    @Override
    public void shutdown() {
      // No-op.
    }
  }

  @BeforeEach
  void setUp() {
    Game testGame = new TestGame();
    engine = new Engine(testGame, MainLoopPolicy.skip());
    engine.init();

    world = engine.getWorld();
    resourceManager = engine.getResourceManager();
    camera = engine.getCamera();
    audioManager = engine.getAudioManager();
  }

  @AfterEach
  void tearDown() {
    if (engine != null) {
      engine.shutdown();
    }
  }
}
