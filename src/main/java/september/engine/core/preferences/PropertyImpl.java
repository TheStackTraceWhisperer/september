package september.engine.core.preferences;

import java.util.Objects;

/**
 * Implementation of Property interface that tracks changes and handles serialization.
 */
final class PropertyImpl<T> implements Property<T> {
    
    private final String key;
    private final T defaultValue;
    private final PropertyType<T> type;
    private final PreferencesServiceImpl service;
    
    private T currentValue;
    private T lastSavedValue;
    
    PropertyImpl(String key, T defaultValue, PropertyType<T> type, PreferencesServiceImpl service) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.service = service;
        
        // Load initial value from preferences
        this.lastSavedValue = loadFromPreferences();
        this.currentValue = this.lastSavedValue;
    }
    
    @Override
    public T get() {
        return currentValue;
    }
    
    @Override
    public void set(T value) {
        if (!Objects.equals(currentValue, value)) {
            currentValue = value;
            service.markDirty(this);
        }
    }
    
    @Override
    public T getDefault() {
        return defaultValue;
    }
    
    @Override
    public boolean isModified() {
        return !Objects.equals(currentValue, defaultValue);
    }
    
    @Override
    public boolean isDirty() {
        return !Objects.equals(currentValue, lastSavedValue);
    }
    
    @Override
    public void revert() {
        if (isDirty()) {
            currentValue = lastSavedValue;
            service.unmarkDirty(this);
        }
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    /**
     * Saves the current value to preferences storage.
     * Called by the preferences service during debounced saves.
     */
    void save() {
        if (isDirty()) {
            String serializedValue = type.serialize(currentValue);
            service.getPreferences().put(key, serializedValue);
            lastSavedValue = currentValue;
        }
    }
    
    /**
     * Loads the value from preferences, returning the default if not found.
     */
    private T loadFromPreferences() {
        String serializedValue = service.getPreferences().get(key, null);
        if (serializedValue == null) {
            return defaultValue;
        }
        
        try {
            return type.deserialize(serializedValue);
        } catch (Exception e) {
            // If deserialization fails, return default and log error
            System.err.println("Failed to deserialize preference '" + key + "': " + e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * Reloads the value from preferences storage.
     * Called during revert operations.
     */
    void reload() {
        T reloadedValue = loadFromPreferences();
        lastSavedValue = reloadedValue;
        currentValue = reloadedValue;
    }
}