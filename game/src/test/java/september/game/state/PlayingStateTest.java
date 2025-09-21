package september.game.state;

import september.engine.core.EngineServices;
import september.engine.ecs.SystemManager;
import september.engine.systems.MovementSystem;
import september.engine.systems.RenderSystem;
import september.game.systems.EnemyAISystem;
import september.game.systems.PlayerInputSystem;

// NOTE: This is a simplified test file due to the lack of a testing framework.

public class PlayingStateTest {

    // Mock for SystemManager
    static class MockSystemManager extends SystemManager {
        public boolean registeredPlayerInput = false;
        public boolean registeredMovement = false;
        public boolean registeredEnemyAI = false;
        public boolean registeredRender = false;
        public boolean cleared = false;

        @Override public void register(september.engine.ecs.ISystem system) {
            if (system instanceof PlayerInputSystem) registeredPlayerInput = true;
            if (system instanceof MovementSystem) registeredMovement = true;
            if (system instanceof EnemyAISystem) registeredEnemyAI = true;
            if (system instanceof RenderSystem) registeredRender = true;
        }
        @Override public void clear() { cleared = true; }
    }

    public static void main(String[] args) {
        System.out.println("Running PlayingState tests...");
        PlayingState state = new PlayingState();

        // Mock services
        MockSystemManager mockSystemManager = new MockSystemManager();

        // Use the builder for a more robust and readable test setup
        EngineServices mockServices = EngineServices.builder()
            .world(new september.engine.ecs.World())
            .systemManager(mockSystemManager)
            .resourceManager(new september.engine.assets.ResourceManager())
            .sceneManager(new september.engine.scene.SceneManager(new java.util.HashMap<>(), null))
            .inputService(new september.engine.core.input.GlfwInputService())
            .gamepadService(new september.engine.core.input.GlfwGamepadService())
            .camera(new september.engine.rendering.Camera())
            .build();

        // Test onEnter
        state.onEnter(mockServices);
        assert mockSystemManager.registeredPlayerInput : "Test Failed: onEnter should register PlayerInputSystem";
        assert mockSystemManager.registeredMovement : "Test Failed: onEnter should register MovementSystem";
        assert mockSystemManager.registeredEnemyAI : "Test Failed: onEnter should register EnemyAISystem";
        assert mockSystemManager.registeredRender : "Test Failed: onEnter should register RenderSystem";
        System.out.println("- onEnter test PASSED");

        // Test onExit
        state.onExit(mockServices);
        assert mockSystemManager.cleared : "Test Failed: onExit should clear systems";
        System.out.println("- onExit test PASSED");

        System.out.println("All PlayingState tests passed!");
    }
}
