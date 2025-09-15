package september.game.preferences;

import september.engine.core.preferences.PreferencesService;
import september.engine.core.preferences.Property;
import september.engine.core.preferences.PropertyType;

/**
 * Example demonstrating how to use the engine's preferences system for game settings.
 * This would typically be used to store player preferences, game settings, etc.
 */
public class GamePreferences {
    
    // Game configuration record for JSON serialization
    public record GraphicsSettings(int width, int height, boolean fullscreen, float masterVolume) {
        public static GraphicsSettings defaultSettings() {
            return new GraphicsSettings(1920, 1080, false, 0.8f);
        }
    }
    
    private final Property<String> playerName;
    private final Property<Integer> highScore;
    private final Property<Boolean> soundEnabled;
    private final Property<Float> musicVolume;
    private final Property<GraphicsSettings> graphics;
    
    public GamePreferences(PreferencesService preferencesService) {
        // Create type-safe properties with sensible defaults
        this.playerName = preferencesService.createProperty(
            "game.player.name", 
            "Player", 
            PropertyType.STRING
        );
        
        this.highScore = preferencesService.createProperty(
            "game.highscore", 
            0, 
            PropertyType.INTEGER
        );
        
        this.soundEnabled = preferencesService.createProperty(
            "game.audio.sound_enabled", 
            true, 
            PropertyType.BOOLEAN
        );
        
        this.musicVolume = preferencesService.createProperty(
            "game.audio.music_volume", 
            0.7f, 
            PropertyType.FLOAT
        );
        
        // Use JSON serialization for complex settings
        this.graphics = preferencesService.createProperty(
            "game.graphics.settings",
            GraphicsSettings.defaultSettings(),
            PropertyType.json(GraphicsSettings.class)
        );
    }
    
    // Getters for accessing properties
    public String getPlayerName() {
        return playerName.get();
    }
    
    public void setPlayerName(String name) {
        playerName.set(name);
    }
    
    public int getHighScore() {
        return highScore.get();
    }
    
    public void setHighScore(int score) {
        if (score > highScore.get()) {
            highScore.set(score);
        }
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled.get();
    }
    
    public void setSoundEnabled(boolean enabled) {
        soundEnabled.set(enabled);
    }
    
    public float getMusicVolume() {
        return musicVolume.get();
    }
    
    public void setMusicVolume(float volume) {
        musicVolume.set(Math.max(0.0f, Math.min(1.0f, volume))); // Clamp to 0-1
    }
    
    public GraphicsSettings getGraphicsSettings() {
        return graphics.get();
    }
    
    public void setGraphicsSettings(int width, int height, boolean fullscreen, float masterVolume) {
        graphics.set(new GraphicsSettings(width, height, fullscreen, masterVolume));
    }
    
    // Utility methods
    public boolean hasUnsavedChanges() {
        return playerName.isDirty() || 
               highScore.isDirty() || 
               soundEnabled.isDirty() || 
               musicVolume.isDirty() || 
               graphics.isDirty();
    }
    
    public void revertAllChanges() {
        playerName.revert();
        highScore.revert();
        soundEnabled.revert();
        musicVolume.revert();
        graphics.revert();
    }
    
    public void resetToDefaults() {
        playerName.set(playerName.getDefault());
        highScore.set(highScore.getDefault());
        soundEnabled.set(soundEnabled.getDefault());
        musicVolume.set(musicVolume.getDefault());
        graphics.set(graphics.getDefault());
    }
}