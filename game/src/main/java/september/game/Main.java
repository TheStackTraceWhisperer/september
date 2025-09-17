package september.game;

import lombok.extern.slf4j.Slf4j;
import september.engine.core.Engine;
import september.engine.core.EngineServices;
import september.engine.core.Game;
import september.engine.core.MainLoopPolicy;
import september.engine.ecs.Component;
import september.engine.ecs.components.ColliderComponent;
import september.engine.ecs.components.ControllableComponent;
import september.engine.ecs.components.MovementStatsComponent;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.state.GameState;
import september.game.components.EnemyComponent;
import september.game.components.HealthComponent;
import september.game.components.PlayerComponent;
import september.game.state.PlayingState;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class Main implements Game {

  @Override
  public GameState getInitialState(EngineServices services) {
    // The game starts directly in the "Playing" state for this simple implementation.
    // A full game would return a new MainMenuState() here.
    return new PlayingState();
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
    return registry;
  }

  public static void main(String[] args) {
    try {
      Game myGame = new Main();
      // The Engine takes the Game object and a loop policy.
      Engine gameEngine = new Engine(myGame, september.engine.core.MainLoopPolicy.standard());
      gameEngine.run();
    } catch (Exception e) {
      log.error("A fatal error occurred.", e);
    }
  }
}
