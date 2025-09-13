package september.engine.core.preferences;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.Set;

/**
 * Implementation of PreferencesService with debounced saves and change tracking.
 */
public final class PreferencesServiceImpl implements PreferencesService {
    
    private static final long DEFAULT_DEBOUNCE_DELAY_MS = 500;
    
    private final Preferences preferences;
    private final ScheduledExecutorService scheduler;
    private final Set<PropertyImpl<?>> dirtyProperties;
    private final long debounceDelayMs;
    
    private ScheduledFuture<?> pendingSave;
    private volatile boolean closed = false;
    
    /**
     * Creates a new preferences service using the system preferences for the specified node.
     *
     * @param nodeName the name of the preferences node
     */
    public PreferencesServiceImpl(String nodeName) {
        this(nodeName, DEFAULT_DEBOUNCE_DELAY_MS);
    }
    
    /**
     * Creates a new preferences service with custom debounce delay.
     *
     * @param nodeName the name of the preferences node
     * @param debounceDelayMs the delay in milliseconds before saving changes
     */
    public PreferencesServiceImpl(String nodeName, long debounceDelayMs) {
        this.preferences = Preferences.userNodeForPackage(PreferencesServiceImpl.class).node(nodeName);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "preferences-save-thread");
            t.setDaemon(true);
            return t;
        });
        this.dirtyProperties = ConcurrentHashMap.newKeySet();
        this.debounceDelayMs = debounceDelayMs;
    }
    
    @Override
    public <T> Property<T> createProperty(String key, T defaultValue, PropertyType<T> type) {
        if (closed) {
            throw new IllegalStateException("PreferencesService has been closed");
        }
        return new PropertyImpl<>(key, defaultValue, type, this);
    }
    
    @Override
    public void flush() {
        if (closed) {
            return;
        }
        
        synchronized (this) {
            // Cancel pending save
            if (pendingSave != null) {
                pendingSave.cancel(false);
                pendingSave = null;
            }
            
            // Save immediately
            saveAllDirtyProperties();
        }
    }
    
    @Override
    public void revert() {
        if (closed) {
            return;
        }
        
        synchronized (this) {
            // Cancel pending save
            if (pendingSave != null) {
                pendingSave.cancel(false);
                pendingSave = null;
            }
            
            // Reload all dirty properties from storage
            dirtyProperties.forEach(PropertyImpl::reload);
            dirtyProperties.clear();
        }
    }
    
    @Override
    public Preferences getPreferences() {
        return preferences;
    }
    
    /**
     * Gets the node name for this preferences service (used for testing).
     */
    public String getNodeName() {
        return preferences.name();
    }
    
    @Override
    public void close() {
        if (closed) {
            return;
        }
        
        closed = true;
        
        // Save any pending changes immediately
        flush();
        
        // Shutdown the scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Marks a property as dirty and schedules a debounced save.
     */
    void markDirty(PropertyImpl<?> property) {
        if (closed) {
            return;
        }
        
        synchronized (this) {
            dirtyProperties.add(property);
            
            // Cancel previous save and schedule a new one
            if (pendingSave != null) {
                pendingSave.cancel(false);
            }
            
            pendingSave = scheduler.schedule(this::saveAllDirtyProperties, debounceDelayMs, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * Removes a property from the dirty set.
     */
    void unmarkDirty(PropertyImpl<?> property) {
        synchronized (this) {
            dirtyProperties.remove(property);
        }
    }
    
    /**
     * Saves all dirty properties and clears the dirty set.
     */
    private void saveAllDirtyProperties() {
        if (closed) {
            return;
        }
        
        synchronized (this) {
            try {
                // Save each dirty property
                dirtyProperties.forEach(PropertyImpl::save);
                dirtyProperties.clear();
                
                // Force preferences to flush to backing store
                preferences.flush();
                
            } catch (BackingStoreException e) {
                System.err.println("Failed to flush preferences to backing store: " + e.getMessage());
            } finally {
                pendingSave = null;
            }
        }
    }
}