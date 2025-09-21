package september.engine.systems;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import september.engine.assets.ResourceManager;
import september.engine.core.WindowContext;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ui.components.UIImageComponent;
import september.engine.ui.components.UITransformComponent;
import september.engine.ui.rendering.UIRenderer;

/** The system responsible for rendering all UI elements. */
public class UIRenderSystem implements ISystem {

  private final IWorld world;
  private final UIRenderer renderer;

  private record Renderable(UITransformComponent transform, UIImageComponent image) {}

  public UIRenderSystem(IWorld world, ResourceManager resourceManager, WindowContext window) {
    this.world = world;

    int width, height;
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1);
      IntBuffer pHeight = stack.mallocInt(1);
      GLFW.glfwGetWindowSize(window.handle(), pWidth, pHeight);
      width = pWidth.get(0);
      height = pHeight.get(0);
    }

    this.renderer = new UIRenderer(resourceManager, width, height);

    // Ensure the renderer's projection is updated when the window is resized.
    window.setResizeListener((w, h) -> this.renderer.resize(w, h));
  }

  @Override
  public int getPriority() {
    return Priority.UI_RENDER;
  }

  @Override
  public void update(float deltaTime) {
    var entities = world.getEntitiesWith(UITransformComponent.class, UIImageComponent.class);
    var renderables = new ArrayList<Renderable>();
    for (int entityId : entities) {
      renderables.add(
          new Renderable(
              world.getComponent(entityId, UITransformComponent.class),
              world.getComponent(entityId, UIImageComponent.class)));
    }

    // Sort back-to-front for correct alpha blending
    renderables.sort(Comparator.comparingDouble(r -> r.transform.offset.z));

    renderer.begin();
    for (var renderable : renderables) {
      renderer.submit(renderable.transform, renderable.image);
    }
    renderer.end();
  }
}
