package september.game.state;

import org.joml.Vector3f;
import september.engine.core.EngineServices;
import september.engine.events.EnhancedEventBus;
import september.engine.events.EventListener;
import september.engine.events.UIButtonClickedEvent;
import september.engine.state.GameState;
import september.engine.systems.RenderSystem;
import september.engine.systems.UIRenderSystem;
import september.engine.systems.UISystem;

public class MainMenuState implements GameState {

  private EngineServices services;

  @Override
  public void onEnter(EngineServices services) {
    this.services = services;
    // Load the scene for the main menu.
    services.sceneManager().load("/scenes/main_menu.json", services.world());

    // Position the camera so it can see the scene.
    services.camera().setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // Register the systems needed for this state's behavior.
    var systemManager = services.systemManager();
    systemManager.register(new RenderSystem(services.world(), services.renderer(), services.resourceManager(), services.camera()));
    systemManager.register(new UISystem(services.world(), services.window(), services.inputService(), services.eventBus()));
    systemManager.register(new UIRenderSystem(services.world(), services.resourceManager(), services.window()));

    // Register this state for annotation-based event listening
    if (services.eventBus() instanceof EnhancedEventBus enhancedEventBus) {
      enhancedEventBus.registerAnnotatedListeners(this);
    }
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // The engine's main loop now drives the SystemManager, so this is no longer needed.
  }

  @Override
  public void onExit(EngineServices services) {
    // Unregister from annotation-based event listening
    if (services.eventBus() instanceof EnhancedEventBus enhancedEventBus) {
      enhancedEventBus.unregisterAnnotatedListeners(this);
    }
    services.systemManager().clear();
  }

  /**
   * Handles UI button click events using the new @EventHandler annotation.
   * This demonstrates the Jakarta pattern support.
   */
  @EnhancedEventBus.EventHandler
  public void onButtonClicked(UIButtonClickedEvent event) {
    if ("START_NEW_GAME".equals(event.actionEvent())) {
      services.gameStateManager().changeState(new PlayingState(), services);
    }
  }
}
