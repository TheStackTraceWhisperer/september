package september.engine.systems;

import java.util.ArrayList;
import java.util.Comparator;
import september.engine.assets.ResourceManager;
import september.engine.core.WindowContext;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ui.components.UIImageComponent;
import september.engine.ui.components.UITransformComponent;
import september.engine.ui.rendering.UIRenderer;

/**
 * The system responsible for rendering all UI elements.
 */
public class UIRenderSystem implements ISystem {

  private final IWorld world;
  private final UIRenderer renderer;

  private record Renderable(UITransformComponent transform, UIImageComponent image) {}

  public UIRenderSystem(IWorld world, ResourceManager resourceManager, WindowContext window) {
    this.world = world;
    this.renderer = new UIRenderer(resourceManager, 800, 600);
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
