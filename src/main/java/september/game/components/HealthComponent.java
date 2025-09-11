package september.game.components;

import september.engine.ecs.Component;

/**
 * A component that gives an entity health and makes it susceptible to damage.
 */
public class HealthComponent implements Component {

    private int maxHealth;
    private int currentHealth;

    public HealthComponent(int maxHealth) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive.");
        }
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Reduces the entity's current health by the given amount.
     * Health will not drop below zero.
     * @param amount The amount of damage to take.
     */
    public void takeDamage(int amount) {
        if (amount < 0) return; // Cannot take negative damage
        this.currentHealth = Math.max(0, this.currentHealth - amount);
    }

    /**
     * Checks if the entity is still alive.
     * @return True if current health is greater than zero, false otherwise.
     */
    public boolean isAlive() {
        return this.currentHealth > 0;
    }
}
