package september.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class HealthComponentTest {

    @Test
    @DisplayName("Constructor should initialize current and max health correctly")
    void constructor_initializesHealth() {
        // Arrange
        int maxHealth = 100;

        // Act
        HealthComponent component = new HealthComponent(maxHealth);

        // Assert
        assertThat(component.getMaxHealth()).as("Max health").isEqualTo(maxHealth);
        assertThat(component.getCurrentHealth()).as("Current health").isEqualTo(maxHealth);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("Constructor should throw IllegalArgumentException for non-positive max health")
    void constructor_throwsForInvalidMaxHealth(int invalidMaxHealth) {
        // Act & Assert
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HealthComponent(invalidMaxHealth))
                .withMessage("Max health must be positive.");
    }

    @Test
    @DisplayName("takeDamage should reduce current health by the given amount")
    void takeDamage_reducesCurrentHealth() {
        // Arrange
        HealthComponent component = new HealthComponent(100);

        // Act
        component.takeDamage(30);

        // Assert
        assertThat(component.getCurrentHealth()).isEqualTo(70);
    }

    @Test
    @DisplayName("takeDamage should not reduce health below zero")
    void takeDamage_clampsHealthAtZero() {
        // Arrange
        HealthComponent component = new HealthComponent(50);

        // Act: Deal more damage than the component has health.
        component.takeDamage(100);

        // Assert
        assertThat(component.getCurrentHealth()).isZero();
    }

    @Test
    @DisplayName("takeDamage should not do anything for negative damage amounts")
    void takeDamage_ignoresNegativeAmount() {
        // Arrange
        HealthComponent component = new HealthComponent(100);

        // Act
        component.takeDamage(-50);

        // Assert
        assertThat(component.getCurrentHealth()).isEqualTo(100);
    }

    @Test
    @DisplayName("isAlive should return true when health is greater than zero")
    void isAlive_returnsTrue_whenHealthIsPositive() {
        // Arrange
        HealthComponent component = new HealthComponent(100);
        component.takeDamage(99);

        // Act & Assert
        assertThat(component.isAlive()).isTrue();
    }

    @Test
    @DisplayName("isAlive should return false when health is zero")
    void isAlive_returnsFalse_whenHealthIsZero() {
        // Arrange
        HealthComponent component = new HealthComponent(100);
        component.takeDamage(100);

        // Act & Assert
        assertThat(component.isAlive()).isFalse();
    }
}
