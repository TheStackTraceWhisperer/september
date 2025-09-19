package september.engine.core;

import org.lwjgl.glfw.GLFW;

import java.time.Duration;

/**
 * Loop continuation policies for the application. Intentionally minimal & non-defensive.
 * Invalid inputs (e.g. null) may throw NPE implicitly.
 */
public interface ApplicationLoopPolicy {
  boolean continueRunning(int frames, long windowHandle);

  /**
   * Continue until the window signals it should close.
   */
  public static ApplicationLoopPolicy standard() {
    return (f, h) -> !GLFW.glfwWindowShouldClose(h);
  }

  /**
   * Limit by frame count (0 => no frames). Negative values yield zero frames (f < negative is false initially).
   */
  public static ApplicationLoopPolicy frames(int maxFrames) {
    return (f, h) -> f < maxFrames;
  }

  /**
   * Alias for frames(0).
   */
  public static ApplicationLoopPolicy skip() {
    return frames(0);
  }

  /**
   * Time limited & window-open requirement. Null duration => NPE. Negative treated naturally via toNanos (still compares).
   */
  public static ApplicationLoopPolicy timed(Duration duration) {
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
  public static ApplicationLoopPolicy all(ApplicationLoopPolicy... policies) {
    return (f, h) -> {
      for (ApplicationLoopPolicy p : policies)
        if (!p.continueRunning(f, h)) {
          return false;
        }
      return true;
    };
  }

  /**
   * Logical OR. Empty => false.
   */
  public static ApplicationLoopPolicy any(ApplicationLoopPolicy... policies) {
    return (f, h) -> {
      for (ApplicationLoopPolicy p : policies)
        if (p.continueRunning(f, h)) {
          return true;
        }
      return false;
    };
  }
}
