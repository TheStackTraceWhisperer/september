package september.engine.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemTimerTest {

  @Test
  void update_advances_delta_and_total_time_non_negative_and_monotonic() {
    SystemTimer timer = new SystemTimer();

    // Before first update
    assertTrue(timer.getDeltaTime() == 0f, "Initial delta should be 0");
    assertTrue(timer.getTotalTime() == 0d, "Initial total should be 0");

    timer.update();
    float firstDelta = timer.getDeltaTime();
    double firstTotal = timer.getTotalTime();

    timer.update();
    float secondDelta = timer.getDeltaTime();
    double secondTotal = timer.getTotalTime();

    assertTrue(firstDelta >= 0f, "First delta non-negative");
    assertTrue(firstTotal >= 0d, "First total non-negative");
    assertTrue(secondDelta >= 0f, "Second delta non-negative");
    assertTrue(secondTotal >= firstTotal, "Total time monotonic");
  }
}
