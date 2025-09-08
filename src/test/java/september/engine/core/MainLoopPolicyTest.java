package september.engine.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class MainLoopPolicyTest {

  private MockedStatic<GLFW> glfw;

  @BeforeEach
  void setUp() {
    // Mock the static GLFW methods for all tests
    glfw = mockStatic(GLFW.class);
  }

  @AfterEach
  void tearDown() {
    // Close the static mock
    glfw.close();
  }

  @Nested
  @DisplayName("standard() Policy")
  class StandardPolicy {
    @Test
    @DisplayName("Continues when window should not close")
    void standard_shouldContinue() {
      glfw.when(() -> GLFW.glfwWindowShouldClose(0L)).thenReturn(false);
      MainLoopPolicy policy = MainLoopPolicy.standard();
      assertTrue(policy.continueRunning(0, 0L));
    }

    @Test
    @DisplayName("Stops when window should close")
    void standard_shouldStop() {
      glfw.when(() -> GLFW.glfwWindowShouldClose(0L)).thenReturn(true);
      MainLoopPolicy policy = MainLoopPolicy.standard();
      assertFalse(policy.continueRunning(0, 0L));
    }
  }

  @Nested
  @DisplayName("frames() Policy")
  class FramesPolicy {
    @Test
    @DisplayName("Continues when frame count is less than max")
    void frames_shouldContinue() {
      MainLoopPolicy policy = MainLoopPolicy.frames(10);
      assertTrue(policy.continueRunning(9, 0L));
    }

    @Test
    @DisplayName("Stops when frame count reaches max")
    void frames_shouldStopAtMax() {
      MainLoopPolicy policy = MainLoopPolicy.frames(10);
      assertFalse(policy.continueRunning(10, 0L));
    }

    @Test
    @DisplayName("Stops immediately for zero max frames")
    void frames_shouldStopAtZero() {
      MainLoopPolicy policy = MainLoopPolicy.frames(0);
      assertFalse(policy.continueRunning(0, 0L));
    }

    @Test
    @DisplayName("Stops immediately for negative max frames")
    void frames_shouldStopAtNegative() {
      MainLoopPolicy policy = MainLoopPolicy.frames(-1);
      assertFalse(policy.continueRunning(0, 0L));
    }
  }

  @Test
  @DisplayName("initializeOnly() is an alias for frames(0)")
  void initializeOnly_isAliasForFramesZero() {
    MainLoopPolicy policy = MainLoopPolicy.initializeOnly();
    assertFalse(policy.continueRunning(0, 0L), "Should not run any frames");
    assertTrue(policy.continueRunning(-1, 0L), "Should behave like frames(0)");
  }

  @Nested
  @DisplayName("timed() Policy")
  class TimedPolicy {
    @Test
    @DisplayName("Continues within the time limit")
    void timed_shouldContinue() throws InterruptedException {
      MainLoopPolicy policy = MainLoopPolicy.timed(Duration.ofMillis(50));
      glfw.when(() -> GLFW.glfwWindowShouldClose(0L)).thenReturn(false);
      assertTrue(policy.continueRunning(0, 0L));
    }

    @Test
    @DisplayName("Stops when time limit is exceeded")
    void timed_shouldStop() throws InterruptedException {
      MainLoopPolicy policy = MainLoopPolicy.timed(Duration.ofMillis(10));
      glfw.when(() -> GLFW.glfwWindowShouldClose(0L)).thenReturn(false);
      Thread.sleep(20);
      assertFalse(policy.continueRunning(0, 0L));
    }

    @Test
    @DisplayName("Stops if window closes, even within time limit")
    void timed_shouldStopIfWindowCloses() {
      MainLoopPolicy policy = MainLoopPolicy.timed(Duration.ofHours(1));
      glfw.when(() -> GLFW.glfwWindowShouldClose(0L)).thenReturn(true);
      assertFalse(policy.continueRunning(0, 0L));
    }

    @Test
    @DisplayName("Stops immediately for zero duration")
    void timed_shouldStopForZeroDuration() {
      MainLoopPolicy policy = MainLoopPolicy.timed(Duration.ZERO);
      assertFalse(policy.continueRunning(0, 0L));
    }
  }

  @Nested
  @DisplayName("all() Policy (Logical AND)")
  class AllPolicy {
    @Test
    @DisplayName("Continues if all sub-policies continue")
    void all_shouldContinueIfAllContinue() {
      MainLoopPolicy policy = MainLoopPolicy.all(MainLoopPolicy.frames(10), MainLoopPolicy.frames(20));
      assertTrue(policy.continueRunning(9, 0L));
    }

    @Test
    @DisplayName("Stops if any sub-policy stops")
    void all_shouldStopIfOneStops() {
      MainLoopPolicy policy = MainLoopPolicy.all(MainLoopPolicy.frames(10), MainLoopPolicy.frames(20));
      assertFalse(policy.continueRunning(10, 0L));
    }

    @Test
    @DisplayName("Returns true for empty argument list (vacuous truth)")
    void all_shouldBeTrueWhenEmpty() {
      MainLoopPolicy policy = MainLoopPolicy.all();
      assertTrue(policy.continueRunning(0, 0L));
    }
  }

  @Nested
  @DisplayName("any() Policy (Logical OR)")
  class AnyPolicy {
    @Test
    @DisplayName("Continues if any sub-policy continues")
    void any_shouldContinueIfOneContinues() {
      MainLoopPolicy policy = MainLoopPolicy.any(MainLoopPolicy.frames(5), MainLoopPolicy.frames(10));
      assertTrue(policy.continueRunning(4, 0L)); // Both true
      assertTrue(policy.continueRunning(6, 0L)); // One true
    }

    @Test
    @DisplayName("Stops when all sub-policies stop")
    void any_shouldStopWhenAllStop() {
      MainLoopPolicy policy = MainLoopPolicy.any(MainLoopPolicy.frames(5), MainLoopPolicy.frames(10));
      assertFalse(policy.continueRunning(10, 0L));
    }

    @Test
    @DisplayName("Returns false for empty argument list")
    void any_shouldBeFalseWhenEmpty() {
      MainLoopPolicy policy = MainLoopPolicy.any();
      assertFalse(policy.continueRunning(0, 0L));
    }
  }
}
