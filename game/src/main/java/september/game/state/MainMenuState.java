package september.game.state;

import org.lwjgl.glfw.GLFW;
import september.engine.core.EngineServices;
import september.engine.state.GameState;

public class MainMenuState implements GameState {

  @Override
  public void onEnter(EngineServices services) {
    // Load the scene for the main menu. This will clear the world
    // and load all assets and entities defined in the file.
    services.sceneManager().load("/scenes/main_menu.json", services.world());

    // No game systems (like EnemyAI or Movement) are needed in the main menu.
    // The RenderSystem is registered by the engine and will run automatically.
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // For now, we'll listen for the Enter key to transition to the playing state.
    // A real UI system would handle button clicks.
    if (services.inputService().isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
      services.gameStateManager().changeState(new PlayingState(), services);
    }
  }

  @Override
  public void onExit(EngineServices services) {
    // When we leave the main menu, the SceneManager will automatically clear
    // the world when it loads the next scene. We can also clear systems here
    // if this state had any long-running systems.
    // For now, this can be empty.
  }
}
