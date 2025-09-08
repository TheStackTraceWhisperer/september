package september.game;

import org.junit.jupiter.api.Test;
import september.engine.core.MainLoopPolicy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainIntegrationTest {

  @Test
  void main_runs_without_mocking_when_glfw_is_available() {
    assertDoesNotThrow(() -> new Main(MainLoopPolicy.frames(1)).run());
  }

}
