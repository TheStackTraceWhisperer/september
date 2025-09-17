package september.engine.scene;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import september.engine.assets.AssetLoader;
import september.engine.ecs.Component;
import september.engine.ecs.ComponentRegistry;
import september.engine.ecs.IWorld;
import september.engine.scene.mixin.JomlMixins;

import java.util.Map;

public class SceneManager {
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);
    private final IWorld world;
    private final ComponentRegistry componentRegistry;
    private final ObjectMapper objectMapper;

    public SceneManager(IWorld world, ComponentRegistry componentRegistry) {
        this.world = world;
        this.componentRegistry = componentRegistry;
        this.objectMapper = new ObjectMapper();
        // Register the mix-ins to teach Jackson how to handle JOML types
        this.objectMapper.addMixIn(Matrix4f.class, JomlMixins.Matrix4fMixin.class);
        this.objectMapper.addMixIn(Vector3f.class, JomlMixins.Vector3fMixin.class);
    }

    public void load(String scenePath) {
        try {
            log.info("Loading scene: {}", scenePath);
            String jsonContent = AssetLoader.readResourceToString(scenePath);
            Scene scene = objectMapper.readValue(jsonContent, Scene.class);
            log.info("Successfully parsed scene: {}", scene.name());

            for (var entityDef : scene.entities()) {
                int entityId = world.createEntity();
                log.info("Created entity '{}' with id {}", entityDef.name(), entityId);
                for (Map.Entry<String, Object> componentEntry : entityDef.components().entrySet()) {
                    try {
                        // Get the class from the registry instead of hardcoding the path
                        Class<?> componentClass = componentRegistry.getClassFor(componentEntry.getKey());
                        // Convert the map data to a component instance
                        Object component = objectMapper.convertValue(componentEntry.getValue(), componentClass);
                        world.addComponent(entityId, (Component) component);
                    } catch (IllegalArgumentException e) {
                        log.error("Component type '{}' not registered. Skipping.", componentEntry.getKey(), e);
                    } catch (Exception e) {
                        log.error("Failed to add component {} to entity {}", componentEntry.getKey(), entityDef.name(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to load scene {}", scenePath, e);
        }
    }
}
