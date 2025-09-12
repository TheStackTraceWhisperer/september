package september.game.components;

import september.engine.ecs.components.ColliderComponent;

/**
 * Defines the specific types of colliders for this particular game.
 * This enum implements the engine's generic ColliderType interface.
 */
public enum GameColliderType implements ColliderComponent.ColliderType {
    PLAYER,
    WALL,
    ENEMY,
    ITEM,
    PROJECTILE
}
