package september.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.core.MainLoopPolicy;

import static org.assertj.core.api.Assertions.assertThatCode;

class MainIntegrationTest {

  @Test
  @DisplayName("Main.run() should complete a single frame without throwing an exception")
  void main_runs_without_mocking_when_glfw_is_available() {
    assertThatCode(() -> new Main(MainLoopPolicy.frames(1)).run())
            .doesNotThrowAnyException();
  }

}
