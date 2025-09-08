package september.engine.ecs.components;

import september.engine.ecs.Component;

/**
 * An ECS component that associates an entity with a renderable mesh.
 *
 * This component is a simple data container that holds a string handle.
 * The RenderSystem uses this handle to look up the actual Mesh resource
 * from the ResourceManager.
 */
public class MeshComponent implements Component {

  public final String meshHandle;

  public MeshComponent(String meshHandle) {
    this.meshHandle = meshHandle;
  }
}
