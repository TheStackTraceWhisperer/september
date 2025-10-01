package september.engine.systems;

import io.micronaut.context.event.ApplicationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.WindowContext;
import september.engine.core.input.GlfwInputService;
import september.engine.ecs.IWorld;
import september.engine.events.UIButtonClickedEvent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the UISystemFactory to ensure it correctly creates UI systems with dependencies.
 */
@ExtendWith(MockitoExtension.class)
class UISystemFactoryTest {

  @Mock private WindowContext mockWindow;
  @Mock private GlfwInputService mockInputService;
  @Mock private ApplicationEventPublisher<UIButtonClickedEvent> mockButtonClickedEvent;
  @Mock private IWorld mockWorld;

  private UISystemFactory uiSystemFactory;

  @BeforeEach
  void setUp() {
    uiSystemFactory = new UISystemFactory(
        mockWindow,
        mockInputService,
        mockButtonClickedEvent);
  }

  @Test
  @DisplayName("Factory creates UISystem with correct dependencies")
  void createUISystem_withValidDependencies_returnsConfiguredSystem() {
    // When
    UISystem system = uiSystemFactory.createUISystem(mockWorld);

    // Then
    assertThat(system).isNotNull();
  }

  @Test
  @DisplayName("Factory creates different instances for each call")
  void createUISystem_multipleCalls_returnsDifferentInstances() {
    // When
    UISystem system1 = uiSystemFactory.createUISystem(mockWorld);
    UISystem system2 = uiSystemFactory.createUISystem(mockWorld);

    // Then
    assertThat(system1).isNotSameAs(system2);
  }
}
