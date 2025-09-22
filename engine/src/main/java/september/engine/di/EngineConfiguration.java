package september.engine.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import september.engine.core.GlfwContext;
import september.engine.core.WindowContext;
import september.engine.core.preferences.PreferencesService;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.rendering.gl.OpenGLRenderer;

/**
 * DI configuration factory for the September Engine.
 * Handles complex initialization logic that can't be handled by simple @Singleton annotations.
 */
@Factory
public class EngineConfiguration {

  private static final int INITIAL_WIDTH = 800;
  private static final int INITIAL_HEIGHT = 600;

  @Bean
  public GlfwContext glfwContext() {
    return new GlfwContext();
  }

  @Bean
  public WindowContext windowContext(GlfwContext glfwContext) {
    // GlfwContext dependency ensures GLFW is initialized before creating window
    return new WindowContext(INITIAL_WIDTH, INITIAL_HEIGHT, "September Engine");
  }

  @Bean
  public Camera camera() {
    Camera camera = new Camera(INITIAL_WIDTH, INITIAL_HEIGHT);
    camera.setPerspective(45.0f, (float) INITIAL_WIDTH / INITIAL_HEIGHT, 0.1f, 100.0f);
    return camera;
  }

  @Bean
  public Renderer renderer() {
    return new OpenGLRenderer();
  }

  @Bean
  public PreferencesService preferencesService() {
    return new PreferencesService("september-engine");
  }
}