package september.engine.core.preferences;

import september.game.preferences.GamePreferences;

/**
 * Manual demonstration of the preferences system functionality.
 * This shows how the system would be used in practice.
 */
public class PreferencesDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== September Engine Preferences System Demo ===\n");

        // Create a preferences service for the demo
        try (PreferencesService preferencesService = new PreferencesService("demo")) {

            // Create game preferences wrapper
            GamePreferences gamePrefs = new GamePreferences(preferencesService);

            // Show initial values
            System.out.println("Initial Values:");
            printGamePrefs(gamePrefs);

            // Modify some values
            System.out.println("\nModifying preferences...");
            gamePrefs.setPlayerName("DemoPlayer");
            gamePrefs.setHighScore(42000);
            gamePrefs.setSoundEnabled(false);
            gamePrefs.setMusicVolume(0.3f);
            gamePrefs.setGraphicsSettings(2560, 1440, true, 0.95f);

            System.out.println("After modifications:");
            printGamePrefs(gamePrefs);
            System.out.println("Has unsaved changes: " + gamePrefs.hasUnsavedChanges());

            // Force save
            System.out.println("\nSaving preferences...");
            preferencesService.flush();
            System.out.println("Has unsaved changes: " + gamePrefs.hasUnsavedChanges());

            // Demonstrate JSON serialization by creating a complex property
            System.out.println("\n=== Testing Complex JSON Property ===");
            record GameState(String level, int score, String[] inventory) {}

            Property<GameState> gameState = preferencesService.createProperty(
                "demo.gamestate",
                new GameState("level1", 0, new String[]{"sword", "potion"}),
                PropertyType.json(GameState.class)
            );

            System.out.println("Default game state: " + gameState.get());

            // Modify and save
            gameState.set(new GameState("level5", 15000, new String[]{"magic_sword", "health_potion", "scroll"}));
            System.out.println("Modified game state: " + gameState.get());

            preferencesService.flush();
            System.out.println("Game state saved!");

            // Demonstrate revert functionality
            System.out.println("\n=== Testing Revert Functionality ===");
            gamePrefs.setPlayerName("TempName");
            gamePrefs.setHighScore(99999);
            System.out.println("Temporary changes:");
            System.out.println("Player name: " + gamePrefs.getPlayerName());
            System.out.println("High score: " + gamePrefs.getHighScore());

            System.out.println("\nReverting changes...");
            gamePrefs.revertAllChanges();
            System.out.println("After revert:");
            System.out.println("Player name: " + gamePrefs.getPlayerName());
            System.out.println("High score: " + gamePrefs.getHighScore());

            System.out.println("\n=== Demo Complete ===");
        }
    }

    private static void printGamePrefs(GamePreferences prefs) {
        System.out.println("  Player Name: " + prefs.getPlayerName());
        System.out.println("  High Score: " + prefs.getHighScore());
        System.out.println("  Sound Enabled: " + prefs.isSoundEnabled());
        System.out.println("  Music Volume: " + prefs.getMusicVolume());

        GamePreferences.GraphicsSettings graphics = prefs.getGraphicsSettings();
        System.out.println("  Graphics: " + graphics.width() + "x" + graphics.height() +
                          (graphics.fullscreen() ? " (Fullscreen)" : " (Windowed)") +
                          ", Master Volume: " + graphics.masterVolume());
    }
}
