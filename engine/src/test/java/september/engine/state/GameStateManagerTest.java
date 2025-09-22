package september.engine.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.EngineServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameStateManagerTest {

  @Mock private EngineServices mockServices;
  @Mock private GameState mockState1;
  @Mock private GameState mockState2;
  @Mock private GameState mockState3;

  private GameStateManager stateManager;

  @BeforeEach
  void setUp() {
    stateManager = new GameStateManager();
  }

  @Test
  @DisplayName("State manager should be empty when initialized")
  void stateManager_isEmpty_whenInitialized() {
    assertThat(stateManager.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Push state should make state active and call onEnter")
  void pushState_shouldMakeStateActiveAndCallOnEnter() {
    stateManager.pushState(mockState1, mockServices);

    assertThat(stateManager.isEmpty()).isFalse();
    verify(mockState1).onEnter(mockServices);
  }

  @Test
  @DisplayName("Update should call onUpdate on the current active state")
  void update_shouldCallOnUpdateOnActiveState() {
    stateManager.pushState(mockState1, mockServices);
    float deltaTime = 0.016f;

    stateManager.update(mockServices, deltaTime);

    verify(mockState1).onUpdate(mockServices, deltaTime);
  }

  @Test
  @DisplayName("Update should do nothing when state manager is empty")
  void update_shouldDoNothing_whenEmpty() {
    float deltaTime = 0.016f;

    // Should not throw any exceptions
    stateManager.update(mockServices, deltaTime);

    // No states to verify
  }

  @Test
  @DisplayName("Pop state should remove current state and call onExit")
  void popState_shouldRemoveCurrentStateAndCallOnExit() {
    stateManager.pushState(mockState1, mockServices);
    
    stateManager.popState(mockServices);

    assertThat(stateManager.isEmpty()).isTrue();
    verify(mockState1).onExit(mockServices);
  }

  @Test
  @DisplayName("Pop state should do nothing when state manager is empty")
  void popState_shouldDoNothing_whenEmpty() {
    // Should not throw any exceptions
    stateManager.popState(mockServices);

    assertThat(stateManager.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Multiple states can be pushed and the latest should be active")
  void multipleStates_latestShouldBeActive() {
    stateManager.pushState(mockState1, mockServices);
    stateManager.pushState(mockState2, mockServices);
    
    float deltaTime = 0.016f;
    stateManager.update(mockServices, deltaTime);

    // Only the latest state (mockState2) should receive updates
    verify(mockState1, never()).onUpdate(any(), anyFloat());
    verify(mockState2).onUpdate(mockServices, deltaTime);
  }

  @Test
  @DisplayName("Popping state should return to previous state")
  void popState_shouldReturnToPreviousState() {
    stateManager.pushState(mockState1, mockServices);
    stateManager.pushState(mockState2, mockServices);
    
    stateManager.popState(mockServices);
    
    float deltaTime = 0.016f;
    stateManager.update(mockServices, deltaTime);

    // mockState2 should have been exited, mockState1 should be active again
    verify(mockState2).onExit(mockServices);
    verify(mockState1).onUpdate(mockServices, deltaTime);
  }

  @Test
  @DisplayName("Change state should exit current and enter new state")
  void changeState_shouldExitCurrentAndEnterNew() {
    stateManager.pushState(mockState1, mockServices);
    
    stateManager.changeState(mockState2, mockServices);

    verify(mockState1).onExit(mockServices);
    verify(mockState2).onEnter(mockServices);
    assertThat(stateManager.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("Change state should work even when state manager is empty")
  void changeState_shouldWork_whenEmpty() {
    stateManager.changeState(mockState1, mockServices);

    verify(mockState1).onEnter(mockServices);
    assertThat(stateManager.isEmpty()).isFalse();
    
    // No state to exit
    verify(mockState1, never()).onExit(any());
  }

  @Test
  @DisplayName("State stack should work correctly with multiple push/pop operations")
  void stateStack_shouldWorkWithMultiplePushPopOperations() {
    // Push states: 1 -> 2 -> 3
    stateManager.pushState(mockState1, mockServices);
    stateManager.pushState(mockState2, mockServices);
    stateManager.pushState(mockState3, mockServices);
    
    // Verify state3 is active
    float deltaTime = 0.016f;
    stateManager.update(mockServices, deltaTime);
    verify(mockState3).onUpdate(mockServices, deltaTime);
    
    // Pop state3, state2 should be active
    stateManager.popState(mockServices);
    verify(mockState3).onExit(mockServices);
    
    reset(mockState1, mockState2, mockState3); // Clear previous interactions
    stateManager.update(mockServices, deltaTime);
    verify(mockState2).onUpdate(mockServices, deltaTime);
    verify(mockState1, never()).onUpdate(any(), anyFloat());
    verify(mockState3, never()).onUpdate(any(), anyFloat());
    
    // Pop state2, state1 should be active
    stateManager.popState(mockServices);
    verify(mockState2).onExit(mockServices);
    
    reset(mockState1, mockState2, mockState3);
    stateManager.update(mockServices, deltaTime);
    verify(mockState1).onUpdate(mockServices, deltaTime);
    verify(mockState2, never()).onUpdate(any(), anyFloat());
    verify(mockState3, never()).onUpdate(any(), anyFloat());
    
    // Pop state1, should be empty
    stateManager.popState(mockServices);
    verify(mockState1).onExit(mockServices);
    assertThat(stateManager.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Change state should work correctly in the middle of a stack")
  void changeState_shouldWorkInMiddleOfStack() {
    stateManager.pushState(mockState1, mockServices);
    stateManager.pushState(mockState2, mockServices);
    
    // Change state2 to state3
    stateManager.changeState(mockState3, mockServices);
    
    verify(mockState2).onExit(mockServices);
    verify(mockState3).onEnter(mockServices);
    
    // Verify state3 is now active
    float deltaTime = 0.016f;
    stateManager.update(mockServices, deltaTime);
    verify(mockState3).onUpdate(mockServices, deltaTime);
    
    // Pop should go back to state1
    stateManager.popState(mockServices);
    verify(mockState3).onExit(mockServices);
    
    reset(mockState1, mockState2, mockState3);
    stateManager.update(mockServices, deltaTime);
    verify(mockState1).onUpdate(mockServices, deltaTime);
  }
}
