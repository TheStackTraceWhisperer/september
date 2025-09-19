package september.game.state;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import september.engine.core.EngineServices;
import september.engine.events.StartNewGameEvent;
import september.engine.state.GameState;
import september.engine.systems.RenderSystem;

public class MainMenuState implements GameState {

  @Override
  public void onEnter(EngineServices services) {
    // Load the scene for the main menu.
    services.sceneManager().load("/scenes/main_menu.json", services.world());

    // Position the camera so it can see the scene.
    services.camera().setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // We must register the RenderSystem to draw the scene.
    services.systemManager().register(new RenderSystem(services.world(), services.renderer(), services.resourceManager(), services.camera()));
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // For now, we'll listen for the Enter key to transition to the playing state.
    if (services.inputService().isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
      services.eventBus().publish(new StartNewGameEvent());
    }
  }

  @Override
  public void onExit(EngineServices services) {
    // When we leave the main menu, we should clear the systems
    // to ensure a clean slate for the next state.
    services.systemManager().clear();
  }
}
