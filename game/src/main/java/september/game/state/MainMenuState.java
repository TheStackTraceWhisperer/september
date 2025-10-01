package september.game.state;

import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.joml.Vector3f;
import september.engine.core.EngineServices;
import september.engine.events.UIButtonClickedEvent;
import september.engine.state.GameState;
import september.engine.state.GameStateManager;
import september.engine.systems.SystemFactory;
import september.engine.systems.UISystemFactory;

@Singleton
public class MainMenuState implements GameState {

  private final PlayingState playingState;
  private final GameStateManager gameStateManager;
  private final SystemFactory systemFactory;
  private final UISystemFactory uiSystemFactory;
  private EngineServices services;

  @Inject
  public MainMenuState(
      PlayingState playingState,
      GameStateManager gameStateManager,
      SystemFactory systemFactory,
      UISystemFactory uiSystemFactory) {
    this.playingState = playingState;
    this.gameStateManager = gameStateManager;
    this.systemFactory = systemFactory;
    this.uiSystemFactory = uiSystemFactory;
  }

  @Override
  public void onEnter(EngineServices services) {
    this.services = services;
    // Load the scene for the main menu.
    services.sceneManager().load("/scenes/main_menu.json", services.world());

    // Position the camera so it can see the scene.
    services.camera().setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // Register the systems needed for this state's behavior using factories.
    var systemManager = services.systemManager();
    systemManager.register(systemFactory.createRenderSystem(services.world()));
    systemManager.register(uiSystemFactory.createUISystem(services.world()));
    systemManager.register(systemFactory.createUIRenderSystem(services.world()));
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // The engine's main loop now drives the SystemManager, so this is no longer needed.
  }

  @Override
  public void onExit(EngineServices services) {
    services.systemManager().clear();
  }

  /**
   * Handles UI button click events using Micronaut's @EventListener annotation.
   * This demonstrates the Micronaut event system.
   */
  @EventListener
  public void onButtonClicked(UIButtonClickedEvent event) {
    if ("START_NEW_GAME".equals(event.actionEvent())) {
      gameStateManager.changeState(playingState, services);
    }
  }
}
