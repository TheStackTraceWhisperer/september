package september.engine.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeServiceTest {

    private SystemTimer timeService;

    @BeforeEach
    void setUp() {
        timeService = new SystemTimer();
    }

    @Test
    @DisplayName("Initial state should be zero")
    void initialState() {
        assertEquals(0.0f, timeService.getDeltaTime(), "Initial delta time should be zero");
        assertEquals(0.0, timeService.getTotalTime(), "Initial total time should be zero");
    }

    @Test
    @DisplayName("Update should advance time")
    void updateAdvancesTime() throws InterruptedException {
        // Initial state is zero
        assertEquals(0.0f, timeService.getDeltaTime());
        assertEquals(0.0, timeService.getTotalTime());

        // Wait a bit to ensure nanoTime changes
        Thread.sleep(10);

        timeService.update();

        // After update, time should have advanced
        assertTrue(timeService.getDeltaTime() > 0, "Delta time should be positive after update");
        assertTrue(timeService.getTotalTime() > 0, "Total time should be positive after update");

        double firstTotalTime = timeService.getTotalTime();

        Thread.sleep(10);

        timeService.update();

        assertTrue(timeService.getTotalTime() > firstTotalTime, "Total time should increase on subsequent updates");
    }
}
