package io.thestacktracewhisperer.september;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

  @Test
  void run_success_path_invokes_expected_glfw_calls() {
    long handle = 123L;

    try (MockedStatic<GlfwContext> glfwCtx = Mockito.mockStatic(GlfwContext.class);
         MockedStatic<WindowContext> winCtx = Mockito.mockStatic(WindowContext.class);
         MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {

      // Mocks for context wrappers (so their close() does nothing platform-specific)
      GlfwContext gc = Mockito.mock(GlfwContext.class);
      WindowContext wc = Mockito.mock(WindowContext.class);
      Mockito.when(wc.handle()).thenReturn(handle);
      glfwCtx.when(GlfwContext::open).thenReturn(gc);
      winCtx.when(() -> WindowContext.open(640, 480, "LWJGL Window")).thenReturn(wc);

      // Stub GLFW version retrieval
      glfw.when(() -> GLFW.glfwGetVersion(
        Mockito.any(IntBuffer.class),
        Mockito.any(IntBuffer.class),
        Mockito.any(IntBuffer.class)
      )).thenAnswer(inv -> {
        IntBuffer maj = inv.getArgument(0);
        IntBuffer min = inv.getArgument(1);
        IntBuffer rev = inv.getArgument(2);
        maj.put(0, 3);
        min.put(0, 7);
        rev.put(0, 1);
        return null;
      });
      glfw.when(GLFW::glfwGetVersionString).thenReturn("GLFW test version");

      // Execute (uses a real MemoryStack; permitted since policy only mandates mocking GLFW/GL classes)
      new Main().run();

      // Verifications of GLFW API usage
      glfw.verify(() -> GLFW.glfwGetVersion(
        Mockito.any(IntBuffer.class),
        Mockito.any(IntBuffer.class),
        Mockito.any(IntBuffer.class)));
      glfw.verify(GLFW::glfwGetVersionString);
      glfw.verify(() -> GLFW.glfwSetWindowShouldClose(handle, true));

      // Ensure window/context factories were invoked
      glfwCtx.verify(GlfwContext::open);
      winCtx.verify(() -> WindowContext.open(640, 480, "LWJGL Window"));
    }
  }

  @Test
  void run_wraps_exception_from_glfw_context_in_runtime() {
    try (MockedStatic<GlfwContext> glfwCtx = Mockito.mockStatic(GlfwContext.class)) {
      glfwCtx.when(GlfwContext::open).thenThrow(new IllegalStateException("init failure"));
      assertThrows(RuntimeException.class, () -> new Main().run());
    }
  }
}
