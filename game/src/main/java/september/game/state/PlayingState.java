package september.game.state;

import org.joml.Vector3f;
import september.engine.core.EngineServices;
import september.engine.ecs.components.ControllableComponent;
import september.engine.ecs.components.MovementStatsComponent;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;
import september.engine.systems.MovementSystem;
import september.engine.state.GameState;
import september.game.components.EnemyComponent;
import september.game.components.PlayerComponent;
import september.game.input.InputMappingService;
import september.game.input.MultiDeviceMappingService;
import september.game.systems.EnemyAISystem;
import september.game.systems.PlayerInputSystem;

public class PlayingState implements GameState {
  @Override
  public void onEnter(EngineServices services) {
    // This logic is moved from the old Main.init()

    // --- 1. Load Game Assets ---
    services.resourceManager().loadTexture("player_texture", "textures/player.png");
    services.resourceManager().loadTexture("enemy_texture", "textures/enemy.png");
    float[] vertices = { 0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f };
    int[] indices = {0, 1, 3, 1, 2, 3};
    services.resourceManager().loadProceduralMesh("quad", vertices, indices);

    // --- 2. Configure Engine Services ---
    services.camera().setPosition(new Vector3f(0.0f, 0.0f, 5.0f));

    // --- 3. Define Initial Game State ---
    var world = services.world();
    int playerEntity = world.createEntity();
    world.addComponent(playerEntity, new TransformComponent());
    world.addComponent(playerEntity, new SpriteComponent("player_texture"));
    world.addComponent(playerEntity, new ControllableComponent());
    world.addComponent(playerEntity, new MovementStatsComponent(2.5f));
    world.addComponent(playerEntity, new PlayerComponent());

    int enemyEntity = world.createEntity();
    world.addComponent(enemyEntity, new TransformComponent());
    world.addComponent(enemyEntity, new SpriteComponent("enemy_texture"));
    world.addComponent(enemyEntity, new MovementStatsComponent(2.5f));
    world.addComponent(enemyEntity, new EnemyComponent());

    // --- 4. Register Game-Specific Systems ---
    var systemManager = services.systemManager();
    InputMappingService mappingService = new MultiDeviceMappingService(services.inputService(), services.gamepadService());
    systemManager.register(new PlayerInputSystem(world, mappingService));
    systemManager.register(new MovementSystem(world));
    systemManager.register(new EnemyAISystem(world, services.timeService()));
  }

  @Override
  public void onUpdate(EngineServices services, float deltaTime) {
    // The Engine automatically calls systemManager.updateAll(), so this can be empty for now.
    // Later, this is where we would check for win/loss conditions or transitions.
  }

  @Override
  public void onExit() {
    // We need to clear systems and entities when leaving this state.
    // This will be more robust when we have a SceneManager.
  }
}
