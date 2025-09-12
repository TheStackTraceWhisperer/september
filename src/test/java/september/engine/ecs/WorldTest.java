package september.engine.ecs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorldTest {

    private IWorld world;

    // --- Test Component Classes ---
    private static class PositionComponent {}
    private static class VelocityComponent {}

    // --- Test System Class ---
    private static class TestSystem implements ISystem {
        boolean wasUpdated = false;
        float lastDeltaTime = 0f;
        final List<Integer> updateOrderList;
        final int id;

        TestSystem() {
            this.updateOrderList = null;
            this.id = -1;
        }

        TestSystem(int id, List<Integer> updateOrderList) {
            this.id = id;
            this.updateOrderList = updateOrderList;
        }

        @Override
        public void update(float deltaTime) {
            wasUpdated = true;
            lastDeltaTime = deltaTime;
            if (updateOrderList != null) {
                updateOrderList.add(id);
            }
        }
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

    @Test
    @DisplayName("Update should call update on all registered systems")
    void update_shouldCallSystems() {
        TestSystem system1 = new TestSystem();
        TestSystem system2 = new TestSystem();

        world.registerSystem(system1);
        world.registerSystem(system2);

        float deltaTime = 0.16f;
        world.update(deltaTime);

        assertThat(system1.wasUpdated).isTrue();
        assertThat(system1.lastDeltaTime).isEqualTo(deltaTime);
        assertThat(system2.wasUpdated).isTrue();
        assertThat(system2.lastDeltaTime).isEqualTo(deltaTime);
    }

    @Test
    @DisplayName("Update should call systems in the order they were registered")
    void update_shouldCallSystemsInOrder() {
        List<Integer> updateOrder = new ArrayList<>();
        TestSystem system1 = new TestSystem(1, updateOrder);
        TestSystem system2 = new TestSystem(2, updateOrder);

        world.registerSystem(system1);
        world.registerSystem(system2);

        world.update(0.16f);

        assertThat(updateOrder).containsExactly(1, 2);
    }
}
