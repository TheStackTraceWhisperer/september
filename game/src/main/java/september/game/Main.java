package september.game;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import september.engine.core.ApplicationLoopPolicy;
import september.engine.core.Engine;
import september.engine.core.EngineServices;
import september.engine.core.Game;
import september.engine.ecs.Component;
import september.engine.ecs.components.ColliderComponent;
import september.engine.ecs.components.ControllableComponent;
import september.engine.ecs.components.MovementStatsComponent;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.state.GameState;
import september.engine.ui.components.UIButtonComponent;
import september.engine.ui.components.UIImageComponent;
import september.engine.ui.components.UITransformComponent;
import september.game.components.EnemyComponent;
import september.game.components.HealthComponent;
import september.game.components.PlayerComponent;
import september.game.state.MainMenuState;

@Slf4j
public final class Main implements Game {
  private static final Logger log = LoggerFactory.getLogger(Main.class);
  private EngineServices services;

  @Override
  public void init(EngineServices services) {
    this.services = services;
    // Event handling is now managed by the individual states (e.g., MainMenuState).
  }

  @Override
  public GameState getInitialState(EngineServices services) {
    return new MainMenuState();
  }

  @Override
  public Map<String, Class<? extends Component>> getComponentRegistry() {
    Map<String, Class<? extends Component>> registry = new HashMap<>();
    // Engine Components
    registry.put("TransformComponent", TransformComponent.class);
    registry.put("SpriteComponent", SpriteComponent.class);
    registry.put("ControllableComponent", ControllableComponent.class);
    registry.put("MovementStatsComponent", MovementStatsComponent.class);
    registry.put("ColliderComponent", ColliderComponent.class);
    // Game Components
    registry.put("PlayerComponent", PlayerComponent.class);
    registry.put("EnemyComponent", EnemyComponent.class);
    registry.put("HealthComponent", HealthComponent.class);

    registry.put("UITransformComponent", UITransformComponent.class);
    registry.put("UIImageComponent", UIImageComponent.class);
    registry.put("UIButtonComponent", UIButtonComponent.class);

    return registry;
  }

  public static void main(String[] args) {
    try {
      Game myGame = new Main();
      // The Engine takes the Game object and a loop policy.
      Engine gameEngine = new Engine(myGame, ApplicationLoopPolicy.standard());
      gameEngine.run();
    } catch (Exception e) {
      log.error("A fatal error occurred.", e);
    }
  }
}
