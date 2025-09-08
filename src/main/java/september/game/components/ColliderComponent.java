package september.game.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import september.engine.ecs.Component;

@Getter
@AllArgsConstructor
public final class ColliderComponent implements Component {

    public enum ColliderType {
        PLAYER,
        WALL,
        ENEMY,
        ITEM,
        PROJECTILE
    }

    private final ColliderType type;
    private final int width;
    private final int height;
    private final int offsetX;
    private final int offsetY;

}
