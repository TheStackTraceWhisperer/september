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
    
    PropertyImpl(String key, T defaultValue, PropertyType<T> type, PreferencesServiceImpl service) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.service = service;
        
        // Load initial value from preferences
        this.currentValue = loadFromPreferences();
    }
    
    @Override
    public T get() {
        return currentValue;
    }
    
    @Override
    public void set(T value) {
        if (!Objects.equals(currentValue, value)) {
            currentValue = value;
            // Update the active properties in the service
            String serializedValue = type.serialize(value);
            service.setPropertyValue(key, serializedValue);
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
        // A property is dirty if its current value differs from the saved value
        T savedValue = getSavedValue();
        return !Objects.equals(currentValue, savedValue);
    }
    
    @Override
    public void revert() {
        if (isDirty()) {
            T savedValue = getSavedValue();
            currentValue = savedValue;
            // Update the active properties to match the saved state for this key
            if (savedValue != null) {
                String serializedValue = type.serialize(savedValue);
                service.setPropertyValue(key, serializedValue);
            } else {
                // Remove from active properties if no saved value exists
                service.setPropertyValue(key, null);
            }
        }
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    /**
     * Loads the value from preferences, returning the default if not found.
     */
    private T loadFromPreferences() {
        String serializedValue = service.getPropertyValue(key);
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
     * Gets the saved (baseline) value for this property.
     */
    private T getSavedValue() {
        String serializedValue = service.getSavedPropertyValue(key);
        if (serializedValue == null) {
            return defaultValue;
        }
        
        try {
            return type.deserialize(serializedValue);
        } catch (Exception e) {
            // If deserialization fails, return default
            return defaultValue;
        }
    }
    
    /**
     * Reloads the value from the current active state.
     * Called during revert operations to sync with the service state.
     */
    void reload() {
        T reloadedValue = loadFromPreferences();
        currentValue = reloadedValue;
    }
}