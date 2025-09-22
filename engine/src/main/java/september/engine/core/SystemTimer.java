package september.engine.core;

import jakarta.inject.Singleton;

@Singleton
public class SystemTimer implements TimeService {

  private static final double NANOS_PER_SECOND = 1_000_000_000.0;

  private final long startTimeNanos;
  private long lastFrameNanos;

  private float deltaTimeSeconds;
  private double totalTimeSeconds;

  public SystemTimer() {
    startTimeNanos = System.nanoTime();
    lastFrameNanos = startTimeNanos;
  }

  @Override
  public void update() {
    long currentFrameNanos = System.nanoTime();

    // Calculate delta time
    deltaTimeSeconds = (float) ((currentFrameNanos - lastFrameNanos) / NANOS_PER_SECOND);

    // Calculate total elapsed time
    totalTimeSeconds = (currentFrameNanos - startTimeNanos) / NANOS_PER_SECOND;

    // Update last frame time for the next iteration
    lastFrameNanos = currentFrameNanos;
  }

  @Override
  public float getDeltaTime() {
    return deltaTimeSeconds;
  }

  @Override
  public double getTotalTime() {
    return totalTimeSeconds;
  }
}
