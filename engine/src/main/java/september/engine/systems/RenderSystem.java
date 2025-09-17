package september.engine.systems;

import september.engine.assets.ResourceManager;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;
import september.engine.rendering.Texture;

/**
 * The system responsible for rendering all sprite entities.
 * <p>
 * This system acts as the bridge between the ECS and the rendering engine.
 * It queries the world for entities with a Transform and a Sprite, resolves their
 * texture and mesh resources, and submits them to the Renderer to be drawn.
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

    // Get all entities that have the components required for sprite rendering
    var renderableEntities = world.getEntitiesWith(TransformComponent.class, SpriteComponent.class);

    // For a 2D sprite game, all sprites will use the same underlying quad mesh.
    // We can resolve this once outside the loop for efficiency.
    Mesh quadMesh = resourceManager.resolveMeshHandle("quad");

    for (int entityId : renderableEntities) {
      TransformComponent transform = world.getComponent(entityId, TransformComponent.class);
      SpriteComponent sprite = world.getComponent(entityId, SpriteComponent.class);

      // Use the handle from the SpriteComponent to get the actual Texture resource
      Texture texture = resourceManager.resolveTextureHandle(sprite.textureHandle());

      // Submit the quad mesh, the specific texture, and the transform to the renderer.
      renderer.submit(quadMesh, texture, transform.getTransformMatrix());
    }

    renderer.endScene();
  }
}
