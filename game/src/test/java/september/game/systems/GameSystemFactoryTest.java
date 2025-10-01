package september.game.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.TimeService;
import september.engine.ecs.IWorld;
import september.game.input.InputMappingService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the GameSystemFactory to ensure it correctly creates game systems with dependencies.
 */
@ExtendWith(MockitoExtension.class)
class GameSystemFactoryTest {

  @Mock private InputMappingService mockInputMappingService;
  @Mock private TimeService mockTimeService;
  @Mock private IWorld mockWorld;

  private GameSystemFactory gameSystemFactory;

  @BeforeEach
  void setUp() {
    gameSystemFactory = new GameSystemFactory(
        mockInputMappingService,
        mockTimeService);
  }

  @Test
  @DisplayName("Factory creates PlayerInputSystem with correct dependencies")
  void createPlayerInputSystem_withValidDependencies_returnsConfiguredSystem() {
    // When
    PlayerInputSystem system = gameSystemFactory.createPlayerInputSystem(mockWorld);

    // Then
    assertThat(system).isNotNull();
  }

  @Test
  @DisplayName("Factory creates EnemyAISystem with correct dependencies")
  void createEnemyAISystem_withValidDependencies_returnsConfiguredSystem() {
    // When
    EnemyAISystem system = gameSystemFactory.createEnemyAISystem(mockWorld);

    // Then
    assertThat(system).isNotNull();
  }

  @Test
  @DisplayName("Factory creates CollisionSystem with correct dependencies")
  void createCollisionSystem_withValidDependencies_returnsConfiguredSystem() {
    // When
    CollisionSystem system = gameSystemFactory.createCollisionSystem(mockWorld);

    // Then
    assertThat(system).isNotNull();
  }

  @Test
  @DisplayName("Factory creates different instances for each call")
  void createPlayerInputSystem_multipleCalls_returnsDifferentInstances() {
    // When
    PlayerInputSystem system1 = gameSystemFactory.createPlayerInputSystem(mockWorld);
    PlayerInputSystem system2 = gameSystemFactory.createPlayerInputSystem(mockWorld);

    // Then
    assertThat(system1).isNotSameAs(system2);
  }
}
