package september.engine.core.preferences;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of PreferencesService with explicit save model and change tracking.
 * <p>
 * This hybrid approach combines the best patterns from two designs:
 * <p>
 * FROM USER'S EXPLICIT SAVE MODEL:
 * - Active vs saved Properties objects for clean state tracking
 * - isModified() method comparing active vs saved state
 * - Explicit flush() and revert() operations
 * - Simple Properties-based storage with string serialization
 * - Clear load() → modify → save()/revert() lifecycle
 * <p>
 * FROM TYPE-SAFE APPROACH:
 * - Property<T> abstraction for compile-time type safety
 * - JSON serialization support for complex objects
 * - Individual property dirty state tracking
 * - PropertyType system for pluggable serialization
 * <p>
 * BENEFITS OF HYBRID:
 * - Predictable explicit save model (no automatic debouncing)
 * - Type safety prevents runtime errors from incorrect types
 * - Support for complex objects via JSON
 * - Simple state management using Properties comparison
 * - Backward compatible with existing API
 */
@Slf4j
public final class PreferencesService implements AutoCloseable {

    private static final String PREFERENCES_KEY = "properties_data";

    @Getter
    private final Preferences preferences;
    private final Map<String, Property<?>> propertyCache;

    // Represents the current settings, which can be modified by the user.
    private Properties activeProperties = new Properties();
    // A clean, read-only copy of the last state that was loaded or saved.
    private Properties savedProperties = new Properties();

    private volatile boolean closed = false;

    /**
     * Creates a new preferences service using the system preferences for the specified node.
     *
     * @param nodeName the name of the preferences node
     */
    public PreferencesService(String nodeName) {
        this.preferences = Preferences.userNodeForPackage(PreferencesService.class).node(nodeName);
        this.propertyCache = new ConcurrentHashMap<>();

        // Load existing settings to establish baseline
        load();
    }

    public <T> Property<T> createProperty(String key, T defaultValue, PropertyType<T> type) {
        if (closed) {
            throw new IllegalStateException("PreferencesService has been closed");
        }

        // Use cached property if available, otherwise create new one
        @SuppressWarnings("unchecked")
        Property<T> property = (Property<T>) propertyCache.get(key);
        if (property == null) {
            property = new Property<>(key, defaultValue, type, this);
            propertyCache.put(key, property);
        }
        return property;
    }

    public void flush() {
        if (closed) {
            return;
        }

        save();
    }

    public void revert() {
        if (closed) {
            return;
        }

        // Restore active state from the clean baseline
        this.activeProperties = (Properties) this.savedProperties.clone();

        // Reload all cached properties from the reverted state
        propertyCache.values().forEach(Property::reload);
    }

  /**
     * Gets the node name for this preferences service (used for testing).
     */
    public String getNodeName() {
        return preferences.name();
    }

    /**
     * Determines if the active settings are different from the last saved state.
     * @return true if the active settings have changed.
     */
    public boolean isModified() {
        return !activeProperties.equals(savedProperties);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;

        // Save any pending changes immediately
        flush();
    }

    /**
     * Loads settings from the persistent store, creating a clean baseline.
     */
    private void load() {
        String serializedProps = preferences.get(PREFERENCES_KEY, null);
        Properties loadedProps = new Properties();

        if (serializedProps != null) {
            try (StringReader reader = new StringReader(serializedProps)) {
                loadedProps.load(reader);
            } catch (IOException e) {
                log.error("Error parsing loaded preferences", e);
            }
        }

        // Establish the clean baseline for both states.
        this.savedProperties = (Properties) loadedProps.clone();
        this.activeProperties = (Properties) loadedProps.clone();
    }

    /**
     * Commits the active settings to the persistent store if they have been modified.
     */
    private void save() {
        if (!isModified()) {
            return;
        }

        try (StringWriter writer = new StringWriter()) {
            activeProperties.store(writer, "September Engine - User Preferences");
            preferences.put(PREFERENCES_KEY, writer.toString());
            preferences.flush();

            // The save was successful. Update the clean baseline to the new state.
            this.savedProperties = (Properties) this.activeProperties.clone();

        } catch (IOException | BackingStoreException e) {
            log.error("CRITICAL: Error saving preferences", e);
        }
    }

    /**
     * Sets a preference value in the active, in-memory state.
     */
    void setPropertyValue(String key, String value) {
        if (value == null) {
            activeProperties.remove(key);
        } else {
            activeProperties.setProperty(key, value);
        }
    }

    /**
     * Retrieves a preference value from the current active state.
     * Returns null if no value exists for the key.
     */
    String getPropertyValue(String key) {
        return activeProperties.getProperty(key);
    }

    /**
     * Gets the saved (baseline) value for a property key.
     * Returns null if no saved value exists for the key.
     */
    String getSavedPropertyValue(String key) {
        return savedProperties.getProperty(key);
    }
}
