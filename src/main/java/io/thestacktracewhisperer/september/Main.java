package io.thestacktracewhisperer.september;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

public final class Main implements Runnable {
  private final Logger log;

  public Main() {
    this(LoggerFactory.getLogger(Main.class));
  }

  public Main(Logger log) {
    this.log = log;
  }

  @Override
  public void run() {
    try (GlfwContext ignored = GlfwContext.open();
         MemoryStack stack = MemoryStack.stackPush();
         WindowContext window = WindowContext.open(640, 480, "LWJGL Window")) {

      // Log window handle for visibility in test output
      log.info("Window handle: {}", window.handle());

      IntBuffer maj = stack.mallocInt(1);
      IntBuffer min = stack.mallocInt(1);
      IntBuffer rev = stack.mallocInt(1);
      GLFW.glfwGetVersion(maj, min, rev);

      log.info("GLFW initialized.");
      log.info("GLFW version: {}.{}.{}", maj.get(0), min.get(0), rev.get(0));
      log.info("GLFW version string: {}", GLFW.glfwGetVersionString());

      // Sleep briefly, then request the window to close
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(ie);
      }
      GLFW.glfwSetWindowShouldClose(window.handle(), true);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    new Main().run();
  }
}
