package september.engine.systems;

import september.engine.assets.ResourceManager;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.MeshComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;

/**
 * The system responsible for rendering all visible entities.
 *
 * This system acts as the bridge between the ECS and the rendering engine.
 * It queries the world for entities that have both a Transform and a Mesh,
 * resolves their mesh handles, and submits them to the Renderer to be drawn.
 */
public class RenderSystem implements ISystem {

  private final IWorld world;
  private final Renderer renderer;
  private final ResourceManager resourceManager;
  private final Camera camera;

  public RenderSystem(IWorld world, Renderer renderer, ResourceManager resourceManager, Camera camera) {
    this.world = world;
    this.renderer = renderer;
    this.resourceManager = resourceManager;
    this.camera = camera;
  }

  @Override
  public void update(float deltaTime) {
    renderer.beginScene(camera);

    // Get all entities that have the components required for rendering
    var renderableEntities = world.getEntitiesWith(TransformComponent.class, MeshComponent.class);

    for (int entityId : renderableEntities) {
      TransformComponent transform = world.getComponent(entityId, TransformComponent.class);
      MeshComponent meshComp = world.getComponent(entityId, MeshComponent.class);

      // Use the handle to get the actual Mesh resource
      Mesh mesh = resourceManager.resolveMeshHandle(meshComp.meshHandle);

      if (mesh != null) {
        // Submit the resolved mesh and transform to the renderer
        renderer.submit(mesh, transform.getTransformMatrix());
      }
    }

    renderer.endScene();
  }
}
