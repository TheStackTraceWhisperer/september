package september.game.state;

import org.joml.Vector3f;
import september.engine.core.EngineServices;
import september.engine.ecs.ComponentRegistry;
import september.engine.ecs.components.ControllableComponent;
import september.engine.ecs.components.MovementStatsComponent;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.scene.SceneManager;
import september.engine.state.GameState;
import september.engine.systems.MovementSystem;
import september.engine.systems.RenderSystem;
import september.game.components.EnemyComponent;
import september.game.components.PlayerComponent;
import september.game.input.InputMappingService;
import september.game.input.MultiDeviceMappingService;
import september.game.systems.EnemyAISystem;
import september.game.systems.PlayerInputSystem;

public class PlayingState implements GameState {
  @Override
  public void onEnter(EngineServices services) {
    // --- 1. Create and populate the Component Registry ---
    ComponentRegistry componentRegistry = new ComponentRegistry();
    // Engine components
    componentRegistry.register(TransformComponent.class);
    componentRegistry.register(SpriteComponent.class);
    componentRegistry.register(ControllableComponent.class);
    componentRegistry.register(MovementStatsComponent.class);
    // Game components
    componentRegistry.register(PlayerComponent.class);
    componentRegistry.register(EnemyComponent.class);

    // --- 2. Load Game Assets (This could also be driven by the scene file later) ---
    services.resourceManager().loadTexture("player_texture", "textures/player.png");
    services.resourceManager().loadTexture("enemy_texture", "textures/enemy.png");
    float[] vertices = { 0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f };
    int[] indices = {0, 1, 3, 1, 2, 3};
    services.resourceManager().loadProceduralMesh("quad", vertices, indices);

    // --- 3. Configure Engine Services ---
    services.camera().setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // --- 4. Load the Scene ---
    // The SceneManager now uses the registry to know which components are available.
    SceneManager sceneManager = new SceneManager(services.world(), componentRegistry);
    sceneManager.load("/scenes/playing_scene.json");

    // --- 5. Register Game-Specific Systems ---
    var systemManager = services.systemManager();
    InputMappingService mappingService = new MultiDeviceMappingService(services.inputService(), services.gamepadService());
    systemManager.register(new PlayerInputSystem(services.world(), mappingService));
    systemManager.register(new MovementSystem(services.world()));
    systemManager.register(new EnemyAISystem(services.world(), services.timeService()));
    // The RenderSystem is crucial for drawing anything.
    systemManager.register(new RenderSystem(services.world(), services.renderer(), services.resourceManager(), services.camera()));
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // This is the core of the game logic for this state.
    // We update all registered systems each frame.
    services.systemManager().updateAll(deltaTime);

    // Later, this is where we would check for win/loss conditions or transitions.
  }

  @Override
  public void onExit(EngineServices services) {
    // We need to clear systems and entities when leaving this state.
    // This will be more robust when we have a SceneManager.
  }
}
