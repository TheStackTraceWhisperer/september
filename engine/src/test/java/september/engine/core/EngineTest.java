package september.engine.core;

import io.micronaut.context.ApplicationContext;
import september.engine.ecs.ISystem;
import september.engine.state.GameState;

import java.util.Collections;
import java.util.Map;

// NOTE: This is a simplified test file due to the lack of a testing framework.

public class EngineTest {

  // Mock for ISystem to track if it was updated
  static class MockSystem implements ISystem {
    public boolean updated = false;

    @Override
    public void update(float deltaTime) {
      updated = true;
    }
  }

  // Mock for GameState to register the mock system
  static class MockGameState implements GameState {
    private final MockSystem systemToRegister;

    public MockGameState(MockSystem system) {
      this.systemToRegister = system;
    }

    @Override
    public void onEnter(EngineServices services) {
      services.systemManager().register(systemToRegister);
    }

    @Override
    public void onUpdate(EngineServices services, float deltaTime) {

    }

    @Override
    public void onExit(EngineServices services) {

    }
  }

  // Mock for Game to provide the initial state
  static class MockGame implements Game {
    private final MockGameState initialState;

    public MockGame(MockGameState state) {
      this.initialState = state;
    }

    @Override
    public void init(EngineServices services) {

    }

    @Override
    public GameState getInitialState(EngineServices services, ApplicationContext applicationContext) {
      return initialState;
    }

    @Override
    public Map<String, Class<? extends september.engine.ecs.Component>> getComponentRegistry() {
      return Collections.emptyMap();
    }
  }

  // Mock for ApplicationLoopPolicy to run the loop only once
  static class MockApplicationLoopPolicy implements ApplicationLoopPolicy {
    private boolean hasRun = false;

    @Override
    public boolean continueRunning(int frame, long window) {
      if (hasRun) {
        return false;
      }
      hasRun = true;
      return true;
    }
  }

  public static void main(String[] args) {
    //System.out.println("Running Engine tests...");

    // 1. Set up all the mocks
    MockSystem mockSystem = new MockSystem();
    MockGameState mockState = new MockGameState(mockSystem);
    MockGame mockGame = new MockGame(mockState);
    MockApplicationLoopPolicy mockLoopPolicy = new MockApplicationLoopPolicy();

    // 2. Create the engine with our mocks
    Engine engine = new Engine(mockGame, mockLoopPolicy);

    // 3. Run the engine. This will init, run the main loop once, and shutdown.
    engine.run();

    // 4. Assert that our mock system was updated by the engine's loop.
    assert mockSystem.updated : "Test Failed: Engine main loop did not update systems.";

    //System.out.println("- Main loop update test PASSED");
    //System.out.println("All Engine tests passed!");
  }
}
