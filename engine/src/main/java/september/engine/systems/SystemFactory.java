package september.engine.systems;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import september.engine.assets.ResourceManager;
import september.engine.core.WindowContext;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;
import september.engine.rendering.gl.OpenGLRenderer;

/**
 * Factory for creating engine systems with proper dependency injection.
 * This centralizes system creation and ensures consistent dependency management.
 */
@Singleton
public class SystemFactory {

  private final ResourceManager resourceManager;
  private final WindowContext window;
  private final OpenGLRenderer renderer;
  private final Camera camera;

  @Inject
  public SystemFactory(
      ResourceManager resourceManager,
      WindowContext window,
      OpenGLRenderer renderer,
      Camera camera) {
    this.resourceManager = resourceManager;
    this.window = window;
    this.renderer = renderer;
    this.camera = camera;
  }

  /**
   * Creates a RenderSystem for the given world.
   *
   * @param world The world to render
   * @return A configured RenderSystem
   */
  public RenderSystem createRenderSystem(IWorld world) {
    return new RenderSystem(world, renderer, resourceManager, camera);
  }

  /**
   * Creates a UIRenderSystem for the given world.
   *
   * @param world The world containing UI entities
   * @return A configured UIRenderSystem
   */
  public UIRenderSystem createUIRenderSystem(IWorld world) {
    return new UIRenderSystem(world, resourceManager, window);
  }

  /**
   * Creates a MovementSystem for the given world.
   *
   * @param world The world containing movable entities
   * @return A configured MovementSystem
   */
  public MovementSystem createMovementSystem(IWorld world) {
    return new MovementSystem(world);
  }
}
