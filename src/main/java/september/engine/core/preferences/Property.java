package september.engine.core.preferences;

/**
 * Type-safe property wrapper that provides access to a preference value
 * with automatic serialization/deserialization and change tracking.
 *
 * @param <T> the type of the property value
 */
public interface Property<T> {
    
    /**
     * Gets the current value of the property.
     * If no value has been set, returns the default value.
     *
     * @return the current property value
     */
    T get();
    
    /**
     * Sets the property value. The change will be saved according to
     * the preferences service's debouncing policy.
     *
     * @param value the new value to set
     */
    void set(T value);
    
    /**
     * Gets the default value for this property.
     *
     * @return the default value
     */
    T getDefault();
    
    /**
     * Returns true if the current value differs from the default.
     *
     * @return true if the value has been modified from default
     */
    boolean isModified();
    
    /**
     * Returns true if there are unsaved changes to this property.
     *
     * @return true if changes are pending save
     */
    boolean isDirty();
    
    /**
     * Reverts this property to its last saved value.
     */
    void revert();
    
    /**
     * Gets the preference key for this property.
     *
     * @return the preference key
     */
    String getKey();
}