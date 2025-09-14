package september.engine.core.preferences;

import java.util.prefs.Preferences;

/**
 * Service interface for managing application preferences with type-safe property abstractions.
 * Provides JSON support, debounced saves, and revert functionality.
 */
public interface PreferencesService extends AutoCloseable {
    
    /**
     * Creates a new property with the specified key and default value.
     *
     * @param <T> the type of the property value
     * @param key the preference key
     * @param defaultValue the default value to use if no value is stored
     * @param type the property type for serialization/deserialization
     * @return a Property instance for type-safe access
     */
    <T> Property<T> createProperty(String key, T defaultValue, PropertyType<T> type);
    
    /**
     * Forces immediate save of all pending changes.
     * Normally saves are debounced, but this bypasses the delay.
     */
    void flush();
    
    /**
     * Reverts all unsaved changes to their last persisted values.
     */
    void revert();
    
    /**
     * Gets the underlying Java Preferences node for direct access if needed.
     * Use with caution as direct modifications bypass the property system.
     *
     * @return the Preferences node
     */
    Preferences getPreferences();
}