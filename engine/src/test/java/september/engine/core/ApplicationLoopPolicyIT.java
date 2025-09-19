package september.engine.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for MainLoopPolicy, verifying its behavior against a live GLFW window handle.
 */
class ApplicationLoopPolicyIT extends EngineTestHarness {

    @Test
    @DisplayName("frames() policy should continue for the specified number of frames")
    void frames_policy_continuesForCorrectFrameCount() {
        // Arrange
        final int frameCount = 5;
        ApplicationLoopPolicy policy = ApplicationLoopPolicy.frames(frameCount);
        long windowHandle = engine.getWindow().handle();

        // Act & Assert
        for (int i = 0; i < frameCount; i++) {
            assertThat(policy.continueRunning(i, windowHandle))
                    .as("Should continue running for frame %d", i)
                    .isTrue();
        }
        assertThat(policy.continueRunning(frameCount, windowHandle))
                .as("Should stop running after frame %d", frameCount)
                .isFalse();
    }

    @Test
    @DisplayName("timed() policy should stop after the specified duration")
    void timed_policy_stopsAfterDuration() throws InterruptedException {
        // Arrange
      Duration duration = Duration.ofMillis(100);
      ApplicationLoopPolicy policy = ApplicationLoopPolicy.timed(duration);
        long windowHandle = engine.getWindow().handle();

        // Act & Assert
        assertThat(policy.continueRunning(0, windowHandle))
                .as("Should continue running at the start")
                .isTrue();

        // Wait for a time longer than the policy's duration
        Thread.sleep((long) (duration.toMillis() + 50));

        assertThat(policy.continueRunning(1, windowHandle))
                .as("Should stop running after the duration has passed")
                .isFalse();
    }

    @Test
    @DisplayName("standard() policy should continue as long as the window is open")
    void standard_policy_continuesWhenWindowIsOpen() {
        // Arrange
        ApplicationLoopPolicy policy = ApplicationLoopPolicy.standard();
        long windowHandle = engine.getWindow().handle();

        // The harness ensures the window is open, so glfwWindowShouldClose should be false.
        // We cannot easily test the "window closed" case without user interaction,
        // but we can and should test the primary "window is open" case.

        // Act & Assert
        assertThat(policy.continueRunning(0, windowHandle))
                .as("Standard policy should continue when window is open")
                .isTrue();
    }

    @Test
    @DisplayName("never() policy should always return false")
    void skip_policy_alwaysReturnsFalse() {
        // Arrange
        ApplicationLoopPolicy policy = ApplicationLoopPolicy.skip();
        long windowHandle = engine.getWindow().handle();

        // Act & Assert
        assertThat(policy.continueRunning(0, windowHandle)).isFalse();
        assertThat(policy.continueRunning(100, windowHandle)).isFalse();
    }
}
