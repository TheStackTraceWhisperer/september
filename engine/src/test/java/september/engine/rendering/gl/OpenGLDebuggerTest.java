package september.engine.rendering.gl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.EngineTestHarness;

import java.util.function.IntSupplier;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.mockito.Mockito.when;

/**
 * Contains both integration smoke tests and pure unit tests for the OpenGLDebugger.
 */
@ExtendWith(MockitoExtension.class)
class OpenGLDebuggerTest {

  // Integration smoke test that requires a live GL context.
  // We create a nested class to apply the harness only to this specific test.
  static class OpenGLDebuggerIntegrationTest extends EngineTestHarness {
    @Test
    @DisplayName("OpenGLDebugger should initialize and clean up without errors in a live context")
    void debugger_initializesAndCleansUp_withoutError() {
      assertThatCode(() -> {
        OpenGLDebugger.init();
        OpenGLDebugger.cleanup();
      }).doesNotThrowAnyException();
    }
  }

  // --- Unit tests for checkErrors that do not require a live GL context ---

  @Test
  @DisplayName("checkErrors should not throw an exception when there are no errors")
  void checkErrors_doesNotThrow_whenNoError() {
    // Arrange
    IntSupplier noErrorSupplier = () -> GL_NO_ERROR;

    // Act & Assert
    assertThatCode(() -> OpenGLDebugger.checkErrors(noErrorSupplier))
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("checkErrors should throw a RuntimeException with a single formatted error")
  void checkErrors_throwsException_forSingleError() {
    // Arrange
    IntSupplier singleErrorSupplier = Mockito.mock(IntSupplier.class);
    when(singleErrorSupplier.getAsInt()).thenReturn(GL_INVALID_ENUM, GL_NO_ERROR);

    // Act & Assert
    assertThatThrownBy(() -> OpenGLDebugger.checkErrors(singleErrorSupplier))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("OpenGL error(s): GL_INVALID_ENUM");
  }

  @Test
  @DisplayName("checkErrors should throw a RuntimeException with multiple formatted errors")
  void checkErrors_throwsException_forMultipleErrors() {
    // Arrange
    IntSupplier multipleErrorSupplier = Mockito.mock(IntSupplier.class);
    when(multipleErrorSupplier.getAsInt()).thenReturn(GL_INVALID_VALUE, GL_INVALID_OPERATION, GL_NO_ERROR);

    // Act & Assert
    assertThatThrownBy(() -> OpenGLDebugger.checkErrors(multipleErrorSupplier))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("OpenGL error(s): GL_INVALID_VALUE GL_INVALID_OPERATION");
  }

  @Test
  @DisplayName("checkErrors should correctly format an unknown error code")
  void checkErrors_formatsUnknownError() {
    // Arrange
    IntSupplier unknownErrorSupplier = Mockito.mock(IntSupplier.class);
    when(unknownErrorSupplier.getAsInt()).thenReturn(0x9999, GL_NO_ERROR);

    // Act & Assert
    assertThatThrownBy(() -> OpenGLDebugger.checkErrors(unknownErrorSupplier))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("OpenGL error(s): UNKNOWN_ERROR(39321)");
  }
}
