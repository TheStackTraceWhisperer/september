package september.engine.core;

/**
 * A functional interface for listeners that respond to window resize events.
 */
@FunctionalInterface
public interface WindowResizeListener {
  /**
   * Called when the window's framebuffer is resized.
   *
   * @param width  The new width, in pixels.
   * @param height The new height, in pixels.
   */
  void onResize(int width, int height);
}

