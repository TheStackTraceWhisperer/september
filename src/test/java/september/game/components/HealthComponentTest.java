package september.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class HealthComponentTest {

    @Test
    @DisplayName("Constructor should initialize current and max health correctly")
    void constructor_initializesHealth() {
        // Arrange
        int maxHealth = 100;

        // Act
        HealthComponent component = new HealthComponent(maxHealth);

        // Assert
        assertAll("Health initialization",
                () -> assertEquals(maxHealth, component.getMaxHealth(), "Max health should be set correctly."),
                () -> assertEquals(maxHealth, component.getCurrentHealth(), "Current health should be initialized to max health.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("Constructor should throw IllegalArgumentException for non-positive max health")
    void constructor_throwsForInvalidMaxHealth(int invalidMaxHealth) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new HealthComponent(invalidMaxHealth),
                "Constructor should throw an exception for max health <= 0.");
    }

    @Test
    @DisplayName("takeDamage should reduce current health by the given amount")
    void takeDamage_reducesCurrentHealth() {
        // Arrange
        HealthComponent component = new HealthComponent(100);

        // Act
        component.takeDamage(30);

        // Assert
        assertEquals(70, component.getCurrentHealth(), "Current health should be reduced by the damage amount.");
    }

    @Test
    @DisplayName("takeDamage should not reduce health below zero")
    void takeDamage_clampsHealthAtZero() {
        // Arrange
        HealthComponent component = new HealthComponent(50);

        // Act: Deal more damage than the component has health.
        component.takeDamage(100);

        // Assert
        assertEquals(0, component.getCurrentHealth(), "Current health should not go below zero.");
    }

    @Test
    @DisplayName("takeDamage should not do anything for negative damage amounts")
    void takeDamage_ignoresNegativeAmount() {
        // Arrange
        HealthComponent component = new HealthComponent(100);

        // Act
        component.takeDamage(-50);

        // Assert
        assertEquals(100, component.getCurrentHealth(), "Taking negative damage should not change health.");
    }

    @Test
    @DisplayName("isAlive should return true when health is greater than zero")
    void isAlive_returnsTrue_whenHealthIsPositive() {
        // Arrange
        HealthComponent component = new HealthComponent(100);
        component.takeDamage(99);

        // Act & Assert
        assertTrue(component.isAlive(), "isAlive should be true when current health is positive.");
    }

    @Test
    @DisplayName("isAlive should return false when health is zero")
    void isAlive_returnsFalse_whenHealthIsZero() {
        // Arrange
        HealthComponent component = new HealthComponent(100);
        component.takeDamage(100);

        // Act & Assert
        assertFalse(component.isAlive(), "isAlive should be false when current health is zero.");
    }
}
