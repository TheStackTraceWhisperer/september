package september.game.systems;

import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.ColliderComponent;
import september.engine.ecs.components.TransformComponent;
import september.game.components.GameColliderType;

import java.util.List;

public class CollisionSystem implements ISystem {

    private final IWorld world;

    public CollisionSystem(IWorld world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        List<Integer> entities = world.getEntitiesWith(TransformComponent.class, ColliderComponent.class);

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                int entityA = entities.get(i);
                int entityB = entities.get(j);

                TransformComponent transformA = world.getComponent(entityA, TransformComponent.class);
                ColliderComponent colliderA = world.getComponent(entityA, ColliderComponent.class);

                TransformComponent transformB = world.getComponent(entityB, TransformComponent.class);
                ColliderComponent colliderB = world.getComponent(entityB, ColliderComponent.class);

                if (checkCollision(transformA, colliderA, transformB, colliderB)) {
                    handleCollision(transformA, colliderA, transformB, colliderB);
                }
            }
        }
    }

    private void handleCollision(TransformComponent transformA, ColliderComponent colliderA, TransformComponent transformB, ColliderComponent colliderB) {
        ColliderComponent.ColliderType typeA = colliderA.getType();
        ColliderComponent.ColliderType typeB = colliderB.getType();

        // --- Player vs. Wall Collision ---
        // This is the core of our solid-object collision response.
        // We now compare against the concrete GameColliderType enum.
        if (typeA == GameColliderType.PLAYER && typeB == GameColliderType.WALL) {
            transformA.revertPosition();
        } else if (typeB == GameColliderType.PLAYER && typeA == GameColliderType.WALL) {
            transformB.revertPosition();
        }

        // Future collision types (e.g., PLAYER vs. ENEMY) can be added here.
    }

    private boolean checkCollision(TransformComponent transformA, ColliderComponent colliderA, TransformComponent transformB, ColliderComponent colliderB) {
        // Get integer-based positions for the collision check
        int x1 = (int) (transformA.position.x + colliderA.getOffsetX());
        int y1 = (int) (transformA.position.y + colliderA.getOffsetY());
        int w1 = colliderA.getWidth();
        int h1 = colliderA.getHeight();

        int x2 = (int) (transformB.position.x + colliderB.getOffsetX());
        int y2 = (int) (transformB.position.y + colliderB.getOffsetY());
        int w2 = colliderB.getWidth();
        int h2 = colliderB.getHeight();

        // AABB collision check
        return (x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2);
    }
}
