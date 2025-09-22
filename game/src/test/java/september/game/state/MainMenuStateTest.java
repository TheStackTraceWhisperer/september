package september.game.state;

import september.engine.core.EngineServices;
import september.engine.ecs.SystemManager;
import september.engine.events.EventBus;
import september.engine.events.UIButtonClickedEvent;
import september.engine.state.GameStateManager;
import september.engine.systems.RenderSystem;
import september.engine.systems.UIRenderSystem;
import september.engine.systems.UISystem;

// NOTE: This is a simplified test file due to the lack of a testing framework.
// It uses a main method to run tests and basic assertions.
// Mocks are simplified for demonstration purposes.

public class MainMenuStateTest {

    // Mock for SystemManager
    static class MockSystemManager extends SystemManager {
        public boolean registeredRender = false;
        public boolean registeredUI = false;
        public boolean registeredUIRender = false;
        public boolean cleared = false;
        @Override public void register(september.engine.ecs.ISystem system) {
            if (system instanceof RenderSystem) registeredRender = true;
            if (system instanceof UISystem) registeredUI = true;
            if (system instanceof UIRenderSystem) registeredUIRender = true;
        }
        @Override public void clear() { cleared = true; }
    }

    // Mock for EventBus
    static class MockEventBus extends EventBus {
        public boolean subscribed = false;
        public boolean unsubscribed = false;
        @Override public <T extends september.engine.events.Event> void subscribe(Class<T> eventClass, september.engine.events.EventListener<T> listener) {
            if (eventClass == UIButtonClickedEvent.class) subscribed = true;
        }
        @Override public <T extends september.engine.events.Event> void unsubscribe(Class<T> eventClass, september.engine.events.EventListener<T> listener) {
            if (eventClass == UIButtonClickedEvent.class) unsubscribed = true;
        }
    }

    // Mock for GameStateManager
    static class MockGameStateManager extends GameStateManager {
        public boolean stateChanged = false;
        @Override public void changeState(september.engine.state.GameState newState, EngineServices services) {
            if (newState instanceof PlayingState) stateChanged = true;
        }
    }

    public static void main(String[] args) {
        System.out.println("Running MainMenuState tests...");
        MainMenuState state = new MainMenuState();

        // Mock services
        MockSystemManager mockSystemManager = new MockSystemManager();
        MockEventBus mockEventBus = new MockEventBus();
        MockGameStateManager mockGameStateManager = new MockGameStateManager();

        // Use the builder for a more robust and readable test setup
        EngineServices mockServices = EngineServices.builder()
            .systemManager(mockSystemManager)
            .gameStateManager(mockGameStateManager)
            .eventBus(mockEventBus)
            .build();

        // Test onEnter
        state.onEnter(mockServices);
        assert mockSystemManager.registeredRender : "Test Failed: onEnter should register RenderSystem";
        assert mockSystemManager.registeredUI : "Test Failed: onEnter should register UISystem";
        assert mockSystemManager.registeredUIRender : "Test Failed: onEnter should register UIRenderSystem";
        assert mockEventBus.subscribed : "Test Failed: onEnter should subscribe to events";
        System.out.println("- onEnter test PASSED");

        // Test handle event - using the new annotation-based approach
        // The event handling is now done through @EventHandler annotation
        state.onButtonClicked(new UIButtonClickedEvent("START_NEW_GAME"));
        assert mockGameStateManager.stateChanged : "Test Failed: handle event should change state";
        System.out.println("- handle event test PASSED");

        // Test onExit
        state.onExit(mockServices);
        assert mockSystemManager.cleared : "Test Failed: onExit should clear systems";
        assert mockEventBus.unsubscribed : "Test Failed: onExit should unsubscribe from events";
        System.out.println("- onExit test PASSED");

        System.out.println("All MainMenuState tests passed!");
    }
}
