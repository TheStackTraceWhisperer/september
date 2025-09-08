package september.engine.ecs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WorldTest {

  private IWorld world;

  // --- Test Component Classes ---
  private static class PositionComponent {
  }

  private static class VelocityComponent {
  }

  @BeforeEach
  void setUp() {
    world = new World();
  }

  @Test
  @DisplayName("Create Entity should return unique and sequential IDs")
  void createEntity_shouldReturnUniqueIds() {
    assertEquals(0, world.createEntity());
    assertEquals(1, world.createEntity());
    assertEquals(2, world.createEntity());
  }

  @Test
  @DisplayName("Component can be added, checked, retrieved, and removed")
  void componentLifecycle() {
    int entity = world.createEntity();
    PositionComponent component = new PositionComponent();

    // Add
    assertFalse(world.hasComponent(entity, PositionComponent.class), "Should not have component before adding");
    world.addComponent(entity, component);
    assertTrue(world.hasComponent(entity, PositionComponent.class), "Should have component after adding");

    // Get
    PositionComponent retrieved = world.getComponent(entity, PositionComponent.class);
    assertNotNull(retrieved, "Retrieved component should not be null");
    assertSame(component, retrieved, "Retrieved component should be the same instance");

    // Remove
    world.removeComponent(entity, PositionComponent.class);
    assertFalse(world.hasComponent(entity, PositionComponent.class), "Should not have component after removing");
    assertNull(world.getComponent(entity, PositionComponent.class), "Component should be null after removing");
  }

  @Test
  @DisplayName("Destroying an entity removes it and all its components")
  void destroyEntity_shouldRemoveAllComponents() {
    int entity = world.createEntity();
    world.addComponent(entity, new PositionComponent());
    world.addComponent(entity, new VelocityComponent());

    world.destroyEntity(entity);

    assertFalse(world.hasComponent(entity, PositionComponent.class), "PositionComponent should be gone");
    assertFalse(world.hasComponent(entity, VelocityComponent.class), "VelocityComponent should be gone");
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
    assertTrue(withPosition.contains(entity1));
    assertTrue(withPosition.contains(entity2));
    assertFalse(withPosition.contains(entity3));
    assertEquals(2, withPosition.size());

    // Query for both PositionComponent and VelocityComponent
    List<Integer> withBoth = world.getEntitiesWith(PositionComponent.class, VelocityComponent.class);
    assertTrue(withBoth.contains(entity1));
    assertFalse(withBoth.contains(entity2));
    assertEquals(1, withBoth.size());

    // Query with no components should return all entities
    List<Integer> all = world.getEntitiesWith();
    assertEquals(3, all.size());
    assertTrue(all.contains(entity1));
    assertTrue(all.contains(entity2));
    assertTrue(all.contains(entity3));
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
    assertFalse(withPosition.contains(entity1));
    assertTrue(withPosition.contains(entity2));
    assertEquals(1, withPosition.size());
  }

  @Test
  @DisplayName("Update should call update on all registered systems")
  void update_shouldCallSystems() {
    ISystem system1 = mock(ISystem.class);
    ISystem system2 = mock(ISystem.class);

    world.registerSystem(system1);
    world.registerSystem(system2);

    float deltaTime = 0.16f;
    world.update(deltaTime);

    // Verify that update was called on both systems with the correct delta time
    verify(system1, times(1)).update(deltaTime);
    verify(system2, times(1)).update(deltaTime);
  }

  @Test
  @DisplayName("Update should call systems in the order they were registered")
  void update_shouldCallSystemsInOrder() {
    ISystem system1 = mock(ISystem.class);
    ISystem system2 = mock(ISystem.class);

    world.registerSystem(system1);
    world.registerSystem(system2);

    InOrder inOrder = Mockito.inOrder(system1, system2);

    world.update(0.16f);

    inOrder.verify(system1).update(0.16f);
    inOrder.verify(system2).update(0.16f);
  }
}
