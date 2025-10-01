package september.engine.core;

import io.micronaut.context.ApplicationContext;
import september.engine.ecs.Component;
import september.engine.state.GameState;

import java.util.Map;

/**
 * Defines the contract for a game that can be run by the Engine.
 * This interface's sole responsibility is to provide the entry point to the game's
 * state machine.
 */
public interface Game {

  /**
   *
   * @param services
   */
  void init(EngineServices services);

  /**
   * Called once by the Engine to get the initial state of the game.
   *
   * @param services A service locator providing access to all core engine systems.
   * @param applicationContext The DI container for retrieving managed beans
   * @return The first GameState that the engine should run.
   */
  GameState getInitialState(EngineServices services, ApplicationContext applicationContext);


  /**
   * Gets the mapping of component names (as used in scene files) to their
   * concrete Class objects. This allows the SceneManager to deserialize scenes.
   *
   * @return A map of component names to component classes.
   */
  Map<String, Class<? extends Component>> getComponentRegistry();
}
