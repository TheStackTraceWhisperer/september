package september.engine.scene;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import september.engine.ecs.Component;
import september.engine.ecs.IWorld;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class SceneManager {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final Map<String, Class<? extends Component>> componentRegistry;

  public SceneManager(Map<String, Class<? extends Component>> componentRegistry) {
    this.componentRegistry = componentRegistry;
  }

  public void load(String path, IWorld world) {
    log.info("Loading scene: {}", path);

    world.getEntitiesWith().forEach(world::destroyEntity);

    try(InputStream sceneStream = SceneManager.class.getResourceAsStream(path)) {
      if(sceneStream == null) {
        throw new IOException("Scene file not found: " + path);
      }
      Scene scene = MAPPER.readValue(sceneStream, Scene.class);
      log.info("Successfully parsed scene: {}", scene.name());

      for(EntityTemplate template : scene.entities()) {
        int entity = world.createEntity();
        log.info("creating entity {} with id {}", template.name(), entity);

        for(Map.Entry<String, Object> componentEntity : template.components().entrySet()) {
          String componentName = componentEntity.getKey();
          Class<? extends Component> componentClass = componentRegistry.get(componentName);

          if(componentClass != null) {
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

}
