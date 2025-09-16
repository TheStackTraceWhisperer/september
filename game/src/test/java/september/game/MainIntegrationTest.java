package september.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.core.Engine;
import september.engine.core.Game;
import september.engine.core.MainLoopPolicy;

import static org.assertj.core.api.Assertions.assertThatCode;

class MainIntegrationTest {

  @Test
  @DisplayName("Engine should run a single frame of the Game without throwing an exception")
  void engine_runs_game_without_error() {
    // Arrange: Create an instance of our Game implementation with a policy to run only one frame.
    Game game = new Main();

    // Act & Assert: The test now correctly creates an Engine, gives it the Game to run,
    // and invokes the engine's run loop.
    assertThatCode(() -> {
      Engine engine = new Engine(game, MainLoopPolicy.frames(1));
      engine.run();
    }).doesNotThrowAnyException();
  }
}
