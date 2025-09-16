package september.game;

import lombok.extern.slf4j.Slf4j;
import september.engine.core.Engine;
import september.engine.core.EngineServices;
import september.engine.core.Game;
import september.engine.core.MainLoopPolicy;
import september.engine.state.GameState;
import september.game.state.PlayingState;

@Slf4j
public final class Main implements Game {

  @Override
  public GameState getInitialState(EngineServices services) {
    // The game starts directly in the "Playing" state for this simple implementation.
    // A full game would return a new MainMenuState() here.
    return new PlayingState();
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
