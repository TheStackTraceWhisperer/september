package september.game.state;

import org.joml.Vector3f;
import september.engine.core.EngineServices;
import september.engine.events.Event;
import september.engine.events.EventListener;
import september.engine.events.UIButtonClickedEvent;
import september.engine.state.GameState;
import september.engine.systems.RenderSystem;
import september.engine.systems.UIRenderSystem;
import september.engine.systems.UISystem;

public class MainMenuState implements GameState, EventListener<UIButtonClickedEvent> {

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

    // Subscribe to UI events
    services.eventBus().subscribe(UIButtonClickedEvent.class, this);
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // The engine's main loop now drives the SystemManager, so this is no longer needed.
  }

  @Override
  public void onExit(EngineServices services) {
    // Unsubscribe from events and clear systems to ensure a clean slate for the next state.
    services.eventBus().unsubscribe(UIButtonClickedEvent.class, this);
    services.systemManager().clear();
  }

  @Override
  public void handle(UIButtonClickedEvent event) {
    if ("START_NEW_GAME".equals(event.actionEvent())) {
      services.gameStateManager().changeState(new PlayingState(), services);
    }
  }
}
