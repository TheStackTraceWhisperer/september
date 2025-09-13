package september.game.preferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import september.engine.core.preferences.PreferencesService;
import september.engine.core.preferences.PreferencesServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

class GamePreferencesTest {
    
    private PreferencesService preferencesService;
    private GamePreferences gamePreferences;
    
    @BeforeEach
    void setUp() {
        String testNodeName = "test-game-prefs-" + System.currentTimeMillis();
        preferencesService = new PreferencesServiceImpl(testNodeName, 50);
        gamePreferences = new GamePreferences(preferencesService);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        if (preferencesService != null) {
            preferencesService.close();
        }
    }
    
    @Test
    void defaultValues_shouldBeCorrect() {
        // Then
        assertThat(gamePreferences.getPlayerName()).isEqualTo("Player");
        assertThat(gamePreferences.getHighScore()).isEqualTo(0);
        assertThat(gamePreferences.isSoundEnabled()).isTrue();
        assertThat(gamePreferences.getMusicVolume()).isEqualTo(0.7f);
        
        GamePreferences.GraphicsSettings graphics = gamePreferences.getGraphicsSettings();
        assertThat(graphics.width()).isEqualTo(1920);
        assertThat(graphics.height()).isEqualTo(1080);
        assertThat(graphics.fullscreen()).isFalse();
        assertThat(graphics.masterVolume()).isEqualTo(0.8f);
    }
    
    @Test
    void setPlayerName_shouldUpdateValue() {
        // When
        gamePreferences.setPlayerName("TestPlayer");
        
        // Then
        assertThat(gamePreferences.getPlayerName()).isEqualTo("TestPlayer");
        assertThat(gamePreferences.hasUnsavedChanges()).isTrue();
    }
    
    @Test
    void setHighScore_shouldOnlyIncreaseScore() {
        // Given
        gamePreferences.setHighScore(100);
        preferencesService.flush();
        
        // When - try to set lower score
        gamePreferences.setHighScore(50);
        
        // Then - score should not decrease
        assertThat(gamePreferences.getHighScore()).isEqualTo(100);
        
        // When - set higher score
        gamePreferences.setHighScore(150);
        
        // Then - score should increase
        assertThat(gamePreferences.getHighScore()).isEqualTo(150);
    }
    
    @Test
    void setMusicVolume_shouldClampValues() {
        // When - set volume above 1.0
        gamePreferences.setMusicVolume(1.5f);
        
        // Then - should be clamped to 1.0
        assertThat(gamePreferences.getMusicVolume()).isEqualTo(1.0f);
        
        // When - set volume below 0.0
        gamePreferences.setMusicVolume(-0.5f);
        
        // Then - should be clamped to 0.0
        assertThat(gamePreferences.getMusicVolume()).isEqualTo(0.0f);
    }
    
    @Test
    void setGraphicsSettings_shouldUpdateComplexObject() {
        // When
        gamePreferences.setGraphicsSettings(2560, 1440, true, 0.9f);
        
        // Then
        GamePreferences.GraphicsSettings graphics = gamePreferences.getGraphicsSettings();
        assertThat(graphics.width()).isEqualTo(2560);
        assertThat(graphics.height()).isEqualTo(1440);
        assertThat(graphics.fullscreen()).isTrue();
        assertThat(graphics.masterVolume()).isEqualTo(0.9f);
        assertThat(gamePreferences.hasUnsavedChanges()).isTrue();
    }
    
    @Test
    void revertAllChanges_shouldRestoreOriginalValues() {
        // Given - make some changes
        gamePreferences.setPlayerName("ModifiedPlayer");
        gamePreferences.setHighScore(500);
        gamePreferences.setSoundEnabled(false);
        gamePreferences.setMusicVolume(0.1f);
        
        // When
        gamePreferences.revertAllChanges();
        
        // Then - should be back to defaults
        assertThat(gamePreferences.getPlayerName()).isEqualTo("Player");
        assertThat(gamePreferences.getHighScore()).isEqualTo(0);
        assertThat(gamePreferences.isSoundEnabled()).isTrue();
        assertThat(gamePreferences.getMusicVolume()).isEqualTo(0.7f);
        assertThat(gamePreferences.hasUnsavedChanges()).isFalse();
    }
    
    @Test
    void resetToDefaults_shouldSetDefaultValues() {
        // Given - save some non-default values
        gamePreferences.setPlayerName("SavedPlayer");
        gamePreferences.setHighScore(1000);
        preferencesService.flush();
        
        // When
        gamePreferences.resetToDefaults();
        
        // Then
        assertThat(gamePreferences.getPlayerName()).isEqualTo("Player");
        assertThat(gamePreferences.getHighScore()).isEqualTo(0);
        assertThat(gamePreferences.hasUnsavedChanges()).isTrue(); // Should be marked as dirty since we changed them
    }
    
    @Test
    void persistence_shouldSaveAndLoadCorrectly() throws Exception {
        // Given
        gamePreferences.setPlayerName("PersistentPlayer");
        gamePreferences.setHighScore(999);
        gamePreferences.setSoundEnabled(false);
        gamePreferences.setMusicVolume(0.5f);
        gamePreferences.setGraphicsSettings(3840, 2160, true, 1.0f);
        
        // When - save and create new instance
        preferencesService.flush();
        String testNodeName = ((PreferencesServiceImpl) preferencesService).getNodeName();
        
        try (PreferencesService newService = new PreferencesServiceImpl(testNodeName)) {
            GamePreferences newGamePrefs = new GamePreferences(newService);
            
            // Then - values should be persisted
            assertThat(newGamePrefs.getPlayerName()).isEqualTo("PersistentPlayer");
            assertThat(newGamePrefs.getHighScore()).isEqualTo(999);
            assertThat(newGamePrefs.isSoundEnabled()).isFalse();
            assertThat(newGamePrefs.getMusicVolume()).isEqualTo(0.5f);
            
            GamePreferences.GraphicsSettings graphics = newGamePrefs.getGraphicsSettings();
            assertThat(graphics.width()).isEqualTo(3840);
            assertThat(graphics.height()).isEqualTo(2160);
            assertThat(graphics.fullscreen()).isTrue();
            assertThat(graphics.masterVolume()).isEqualTo(1.0f);
        }
    }
}