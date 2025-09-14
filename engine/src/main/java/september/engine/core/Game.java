package september.engine.core;

import september.engine.ecs.ISystem;
import java.util.Collection;

/**
 * Defines the contract for a game that can be run by the Engine.
 * This interface decouples the generic engine loop from any game-specific logic.
 */
public interface Game {
  /**
   * Called once by the Engine after all core services are initialized.
   * Use this method to load initial assets, create starting entities,
   * and prepare the game for its first frame.
   *
   * @param services A service locator providing access to all core engine systems.
   */
  void init(EngineServices services);

  /**
   * Called by the Engine immediately after init() to retrieve all game-specific systems.
   * These systems will be registered with the world and updated every frame.
   *
   * @return A collection of the game's systems.
   */
  Collection<ISystem> getSystems();

  /**
   * Called once when the engine is shutting down.
   * Use this for any final game-specific cleanup if necessary.
   */
  void shutdown();
}
