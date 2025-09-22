package september.engine.scene;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import september.engine.assets.ResourceManager;
import september.engine.ecs.Component;
import september.engine.ecs.IWorld;
import september.engine.scene.json.CustomJomlModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class SceneManager {
  private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new CustomJomlModule());

  private final Map<String, Class<? extends Component>> componentRegistry;
  private final ResourceManager resourceManager;

  public SceneManager(Map<String, Class<? extends Component>> componentRegistry, ResourceManager resourceManager) {
    this.componentRegistry = componentRegistry;
    this.resourceManager = resourceManager;
  }

  public void load(String path, IWorld world) {
    log.info("Loading scene: {}", path);
    world.getEntitiesWith().forEach(world::destroyEntity);

    try (InputStream sceneStream = SceneManager.class.getResourceAsStream(path)) {
      if (sceneStream == null) {
        throw new IOException("Scene file not found: " + path);
      }
      Scene scene = MAPPER.readValue(sceneStream, Scene.class);
      log.info("Successfully parsed scene: {}", scene.name());

      // Step 1: Load all assets from the manifest
      loadAssets(scene.manifest());

      // Step 2: Create all entities from the templates
      for (EntityTemplate template : scene.entities()) {
        int entity = world.createEntity();
        log.info("creating entity {} with id {}", template.name(), entity);

        for (Map.Entry<String, Object> componentEntity : template.components().entrySet()) {
          String componentName = componentEntity.getKey();
          Class<? extends Component> componentClass = componentRegistry.get(componentName);

          if (componentClass != null) {
            Component component = MAPPER.convertValue(componentEntity.getValue(), componentClass);
            world.addComponent(entity, component);
          } else {
            log.warn("Unknown component type '{}' for entity '{}'", componentName, template.name());
          }
        }
      }
    } catch (Exception e) {
      log.error("Failed to load scene {}", path, e);
    }
  }

  private void loadAssets(AssetManifest manifest) {
    if (manifest == null) {
      return;
    }

    log.info("Loading assets from scene manifest...");
    if (manifest.textures() != null) {
      for (TextureDefinition textureDef : manifest.textures()) {
        resourceManager.loadTexture(textureDef.handle(), textureDef.path());
      }
    }

    if (manifest.meshes() != null) {
      for (MeshDefinition meshDef : manifest.meshes()) {
        resourceManager.loadProceduralMesh(meshDef.handle(), meshDef.vertices(), meshDef.indices());
      }
    }
  }
}
