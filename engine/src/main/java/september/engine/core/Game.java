package september.engine.core;

import september.engine.state.GameState;

/**
 * Defines the contract for a game that can be run by the Engine.
 * This interface's sole responsibility is to provide the entry point to the game's
 * state machine.
 */
public interface Game {
  /**
   * Called once by the Engine to get the initial state of the game.
   *
   * @param services A service locator providing access to all core engine systems.
   * @return The first GameState that the engine should run.
   */
  GameState getInitialState(EngineServices services);
}
