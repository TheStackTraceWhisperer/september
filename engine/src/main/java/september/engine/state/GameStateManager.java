package september.engine.state;

import jakarta.inject.Singleton;
import september.engine.core.EngineServices;

import java.util.Stack;

/**
 * A stack-based Finite State Machine (FSM) for managing the game's high-level states.
 */
@Singleton
public class GameStateManager {
  private final Stack<GameState> stateStack = new Stack<>();

  /**
   * Updates the current active state.
   *
   * @param services  The engine services context.
   * @param deltaTime The time elapsed since the last frame.
   */
  public void update(EngineServices services, float deltaTime) {
    if (!stateStack.isEmpty()) {
      stateStack.peek().onUpdate(services, deltaTime);
    }
  }

  /**
   * Pushes a new state onto the stack, making it the active state.
   *
   * @param state    The new state to activate.
   * @param services The engine services context.
   */
  public void pushState(GameState state, EngineServices services) {
    stateStack.push(state);
    state.onEnter(services);
  }

  /**
   * Removes the current state from the stack, returning to the previous state.
   */
  public void popState(EngineServices services) {
    if (!stateStack.isEmpty()) {
      stateStack.pop().onExit(services);
    }
  }

  /**
   * Pops the current state and pushes a new one in a single, atomic operation.
   *
   * @param state    The new state to activate.
   * @param services The engine services context.
   */
  public void changeState(GameState state, EngineServices services) {
    if (!stateStack.isEmpty()) {
      stateStack.pop().onExit(services);
    }
    stateStack.push(state);
    state.onEnter(services);
  }

  /**
   * Checks if the state manager is empty.
   *
   * @return true if no states are active.
   */
  public boolean isEmpty() {
    return stateStack.isEmpty();
  }
}
