package september.engine.ecs.components;

import september.engine.ecs.Component;

/**
 * A component that stores movement-related stats for an entity.
 */
public record MovementStatsComponent(float speed) implements Component {
}
