package september.engine.core;

import org.lwjgl.glfw.GLFW;

import java.time.Duration;

/**
 * Loop continuation policies for the application. Intentionally minimal & non-defensive.
 * Invalid inputs (e.g. null) may throw NPE implicitly.
 */
public interface MainLoopPolicy {
  boolean continueRunning(int frames, long windowHandle);

  /**
   * Continue until the window signals it should close.
   */
  public static MainLoopPolicy standard() {
    return (f, h) -> !GLFW.glfwWindowShouldClose(h);
  }

  /**
   * Limit by frame count (0 => no frames). Negative values yield zero frames (f < negative is false initially).
   */
  public static MainLoopPolicy frames(int maxFrames) {
    return (f, h) -> f < maxFrames;
  }

  /**
   * Alias for frames(0).
   */
  public static MainLoopPolicy skip() {
    return frames(0);
  }

  /**
   * Time limited & window-open requirement. Null duration => NPE. Negative treated naturally via toNanos (still compares).
   */
  public static MainLoopPolicy timed(Duration duration) {
    final long limitNanos = duration.toNanos();
    final long start = System.nanoTime();
    if (limitNanos == 0L) {
      return (f, h) -> false;
    }
    return (f, h) -> !GLFW.glfwWindowShouldClose(h) && (System.nanoTime() - start) < limitNanos;
  }

  /**
   * Logical AND. Empty => true (vacuous truth).
   */
  public static MainLoopPolicy all(MainLoopPolicy... policies) {
    return (f, h) -> {
      for (MainLoopPolicy p : policies)
        if (!p.continueRunning(f, h)) {
          return false;
        }
      return true;
    };
  }

  /**
   * Logical OR. Empty => false.
   */
  public static MainLoopPolicy any(MainLoopPolicy... policies) {
    return (f, h) -> {
      for (MainLoopPolicy p : policies)
        if (p.continueRunning(f, h)) {
          return true;
        }
      return false;
    };
  }
}
