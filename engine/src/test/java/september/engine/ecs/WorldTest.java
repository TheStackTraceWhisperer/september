package september.engine.ecs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorldTest {

  private IWorld world;

  // --- Test Component Classes ---
  private static class PositionComponent implements Component {
  }

  private static class VelocityComponent implements Component {
  }

  @BeforeEach
  void setUp() {
    world = new World();
  }

  @Test
  @DisplayName("Create Entity should return unique and sequential IDs")
  void createEntity_shouldReturnUniqueIds() {
    assertThat(world.createEntity()).isEqualTo(0);
    assertThat(world.createEntity()).isEqualTo(1);
    assertThat(world.createEntity()).isEqualTo(2);
  }

  @Test
  @DisplayName("Component can be added, checked, retrieved, and removed")
  void componentLifecycle() {
    int entity = world.createEntity();
    PositionComponent component = new PositionComponent();

    // Add
    assertThat(world.hasComponent(entity, PositionComponent.class)).as("Should not have component before adding").isFalse();
    world.addComponent(entity, component);
    assertThat(world.hasComponent(entity, PositionComponent.class)).as("Should have component after adding").isTrue();

    // Get
    PositionComponent retrieved = world.getComponent(entity, PositionComponent.class);
    assertThat(retrieved).as("Retrieved component should not be null").isNotNull();
    assertThat(retrieved).as("Retrieved component should be the same instance").isSameAs(component);

    // Remove
    world.removeComponent(entity, PositionComponent.class);
    assertThat(world.hasComponent(entity, PositionComponent.class)).as("Should not have component after removing").isFalse();
    assertThat(world.getComponent(entity, PositionComponent.class)).as("Component should be null after removing").isNull();
  }

  @Test
  @DisplayName("Destroying an entity removes it and all its components")
  void destroyEntity_shouldRemoveAllComponents() {
    int entity = world.createEntity();
    world.addComponent(entity, new PositionComponent());
    world.addComponent(entity, new VelocityComponent());

    world.destroyEntity(entity);

    assertThat(world.hasComponent(entity, PositionComponent.class)).as("PositionComponent should be gone").isFalse();
    assertThat(world.hasComponent(entity, VelocityComponent.class)).as("VelocityComponent should be gone").isFalse();
  }

  @Test
  @DisplayName("Get Entities With should correctly filter entities by components")
  void getEntitiesWith_shouldFilterCorrectly() {
    int entity1 = world.createEntity(); // Position + Velocity
    world.addComponent(entity1, new PositionComponent());
    world.addComponent(entity1, new VelocityComponent());

    int entity2 = world.createEntity(); // Position only
    world.addComponent(entity2, new PositionComponent());

    int entity3 = world.createEntity(); // No components

    // Query for PositionComponent
    List<Integer> withPosition = world.getEntitiesWith(PositionComponent.class);
    assertThat(withPosition).containsExactlyInAnyOrder(entity1, entity2);

    // Query for both PositionComponent and VelocityComponent
    List<Integer> withBoth = world.getEntitiesWith(PositionComponent.class, VelocityComponent.class);
    assertThat(withBoth).containsExactly(entity1);

    // Query with no components should return all entities
    List<Integer> all = world.getEntitiesWith();
    assertThat(all).containsExactlyInAnyOrder(entity1, entity2, entity3);
  }

  @Test
  @DisplayName("Destroyed entities should not appear in queries")
  void getEntitiesWith_shouldNotIncludeDestroyed() {
    int entity1 = world.createEntity();
    world.addComponent(entity1, new PositionComponent());
    int entity2 = world.createEntity();
    world.addComponent(entity2, new PositionComponent());

    world.destroyEntity(entity1);

    List<Integer> withPosition = world.getEntitiesWith(PositionComponent.class);
    assertThat(withPosition).containsExactly(entity2);
  }
}
