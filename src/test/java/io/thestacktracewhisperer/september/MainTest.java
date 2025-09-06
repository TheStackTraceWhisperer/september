//package io.thestacktracewhisperer.september;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.lwjgl.glfw.GLFW;
//import org.lwjgl.glfw.GLFWErrorCallbackI;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//
//import java.nio.IntBuffer;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@ExtendWith(MockitoExtension.class)
//class MainTest {
//
//  @Mock
//  Logger log;
//
//  @InjectMocks
//  Main main;
//
//  @Test
//  void run_logs_glfw_info_when_init_succeeds() {
//    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
//      // Arrange GLFW
//      long handle = 42L;
//      glfw.when(GLFW::glfwInit).thenReturn(true);
//      glfw.when(() -> GLFW.glfwSetErrorCallback(Mockito.any(GLFWErrorCallbackI.class))).thenReturn(null);
//      glfw.when(() -> GLFW.glfwSetErrorCallback(null)).thenReturn(null);
//      glfw.when(GLFW::glfwGetVersionString).thenReturn("GLFW 9.9.9 Testing");
//      glfw.when(() -> GLFW.glfwGetVersion(
//        Mockito.any(IntBuffer.class),
//        Mockito.any(IntBuffer.class),
//        Mockito.any(IntBuffer.class)
//      )).thenAnswer(inv -> {
//        IntBuffer maj = inv.getArgument(0);
//        IntBuffer min = inv.getArgument(1);
//        IntBuffer rev = inv.getArgument(2);
//        maj.put(0, 3);
//        min.put(0, 7);
//        rev.put(0, 1);
//        return null;
//      });
//      glfw.when(() -> GLFW.glfwCreateWindow(640, 480, "LWJGL Window", 0L, 0L)).thenReturn(handle);
//
//      // Act
//      main.run();
//
//      // Assert logging
//      Mockito.verify(log).info("Window handle: {}", handle);
//      Mockito.verify(log).info("GLFW initialized.");
//      Mockito.verify(log).info("GLFW version: {}.{}.{}", 3, 7, 1);
//      Mockito.verify(log).info("GLFW version string: {}", "GLFW 9.9.9 Testing");
//
//      // Window lifecycle and orderly shutdown of GLFW
//      glfw.verify(() -> GLFW.glfwSetWindowShouldClose(handle, true));
//      glfw.verify(() -> GLFW.glfwDestroyWindow(handle));
//      glfw.verify(() -> GLFW.glfwTerminate());
//      glfw.verify(() -> GLFW.glfwSetErrorCallback(null));
//    }
//  }
//
//  @Test
//  void run_throws_runtime_exception_when_init_fails() {
//    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
//      // Arrange
//      glfw.when(GLFW::glfwInit).thenReturn(false);
//      glfw.when(() -> GLFW.glfwSetErrorCallback(Mockito.any(GLFWErrorCallbackI.class))).thenReturn(null);
//
//      // Act & Assert
//      assertThrows(RuntimeException.class, () -> main.run());
//
//      // Ensure we never terminated since init failed
//      glfw.verify(() -> GLFW.glfwTerminate(), Mockito.never());
//    }
//  }
//}
