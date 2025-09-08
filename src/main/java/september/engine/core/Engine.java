package september.engine.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.GLFW;
import september.engine.assets.ResourceManager;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.rendering.gl.OpenGLRenderer;
import september.engine.systems.RenderSystem;

/**
 * The runtime core of the application.
 * Manages the main loop, orchestrates service lifecycles, and drives the ECS updates.
 */
@Slf4j
@RequiredArgsConstructor
public final class Engine implements Runnable {
  private final IWorld world;
  private final TimeService timeService;
  private final ResourceManager resourceManager;
  private final Camera camera;
  private final MainLoopPolicy loopPolicy;
//
//  public Engine(IWorld world, TimeService timeService, ResourceManager resourceManager, Camera camera, MainLoopPolicy loopPolicy) {
//    this.world = world;
//    this.timeService = timeService;
//    this.resourceManager = resourceManager;
//    this.camera = camera;
//    this.loopPolicy = loopPolicy;
//  }

  @Override
  public void run() {
    // The try-with-resources block safely manages the lifecycles of native resources.
    try (
      GlfwContext ignored = new GlfwContext();
      WindowContext window = new WindowContext(800, 600, "September Engine");
      ResourceManager resources = this.resourceManager // Ensures resources.close() is called
    ) {
      // --- CRITICAL: INITIALIZE RENDERER AND SYSTEMS *AFTER* CONTEXT CREATION ---

      // 1. Create the renderer now that we have a valid OpenGL context.
      Renderer renderer = new OpenGLRenderer();

      // 2. Create and register the systems that depend on the renderer.
      RenderSystem renderSystem = new RenderSystem(world, renderer, resourceManager, camera);
      world.registerSystem(renderSystem);

      // --- MAIN LOOP ---
      int frames = 0;
      while (loopPolicy.continueRunning(frames, window.handle())) {
        GLFW.glfwPollEvents();

        timeService.update();
        float dt = timeService.getDeltaTime();

        world.update(dt);

        window.swapBuffers();
        frames++;
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}

