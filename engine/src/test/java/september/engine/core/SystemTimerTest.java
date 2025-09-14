package september.engine.core;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemTimerTest {

    // Define a tolerance for timer tests, as Thread.sleep is not perfectly precise.
    private static final Offset<Float> TOLERANCE = Offset.offset(0.05f); // 50ms tolerance

    @Test
    @DisplayName("getDeltaTime should be zero before the first update")
    void deltaTime_isZero_beforeFirstUpdate() {
        SystemTimer timer = new SystemTimer();
        assertThat(timer.getDeltaTime()).isZero();
    }

    @Test
    @DisplayName("getDeltaTime should measure the approximate time between updates")
    void deltaTime_measuresTime_betweenUpdates() throws InterruptedException {
        // Arrange
        SystemTimer timer = new SystemTimer();
        long sleepMillis = 100;
        float sleepSeconds = sleepMillis / 1000.0f;

        // Act
        timer.update(); // First update to set the initial time
        Thread.sleep(sleepMillis);
        timer.update(); // Second update to calculate the delta

        // Assert
        assertThat(timer.getDeltaTime()).isCloseTo(sleepSeconds, TOLERANCE);
    }

    @Test
    @DisplayName("getTotalTime should accumulate time over multiple updates")
    void totalTime_accumulates_overUpdates() throws InterruptedException {
        // Arrange
        SystemTimer timer = new SystemTimer();
        long sleepMillis1 = 50;
        long sleepMillis2 = 70;

        // Act
        timer.update();
        Thread.sleep(sleepMillis1);
        timer.update();
        double totalTimeAfterFirstSleep = timer.getTotalTime();

        Thread.sleep(sleepMillis2);
        timer.update();
        double totalTimeAfterSecondSleep = timer.getTotalTime();

        // Assert
        float expectedTotalSeconds = (sleepMillis1 + sleepMillis2) / 1000.0f;
        assertThat((float) totalTimeAfterSecondSleep).isCloseTo(expectedTotalSeconds, TOLERANCE);
        assertThat(totalTimeAfterSecondSleep).isGreaterThan(totalTimeAfterFirstSleep);
    }
}
