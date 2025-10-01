package september.game.state;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.joml.Vector3f;
import september.engine.core.EngineServices;
import september.engine.state.GameState;
import september.engine.systems.SystemFactory;
import september.game.systems.GameSystemFactory;

@Singleton
public class PlayingState implements GameState {
  
  private final SystemFactory systemFactory;
  private final GameSystemFactory gameSystemFactory;
  
  @Inject
  public PlayingState(
      SystemFactory systemFactory,
      GameSystemFactory gameSystemFactory) {
    this.systemFactory = systemFactory;
    this.gameSystemFactory = gameSystemFactory;
  }
  
  @Override
  public void onEnter(EngineServices services) {
    // Step 1: Load the scene, which now includes loading all necessary assets.
    services.sceneManager().load("/scenes/playing_scene.json", services.world());

    // Step 2: Configure any engine services specific to this state.
    services.camera().setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // Step 3: Register the systems that define this state's behavior using factories.
    var world = services.world();
    var systemManager = services.systemManager();

    systemManager.register(gameSystemFactory.createPlayerInputSystem(world));
    systemManager.register(systemFactory.createMovementSystem(world));
    systemManager.register(gameSystemFactory.createEnemyAISystem(world));
    systemManager.register(systemFactory.createRenderSystem(world));
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // The Engine's main loop now drives the SystemManager, so states that
    // simply run systems can have an empty update method. This is a deliberate
    // design choice that could be changed if states need more direct control.
  }

  @Override
  public void onExit(EngineServices services) {
    // When this state exits, we should clean up its resources.
    // A robust implementation would involve the SceneManager and ResourceManager
    // tracking which assets belong to which scene and unloading them.

    // For now, we clear all entities and systems.
    //var services = engine.getServices(); // Assuming a getter on Engine
    services.world().getEntitiesWith().forEach(entityId -> services.world().destroyEntity(entityId));
    services.systemManager().clear();
  }
}
