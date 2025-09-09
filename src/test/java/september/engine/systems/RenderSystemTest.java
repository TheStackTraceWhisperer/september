package september.engine.systems;

import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.assets.ResourceManager;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.TransformComponent;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;
import september.engine.rendering.Texture;
import september.game.components.SpriteComponent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenderSystemTest {

    @Mock private IWorld world;
    @Mock private Renderer renderer;
    @Mock private Camera camera;
    @Mock private ResourceManager resourceManager;
    @Mock private Mesh quadMesh;
    @Mock private Texture texture1;
    @Mock private Texture texture2;

    private RenderSystem renderSystem;

    @BeforeEach
    void setUp() {
        renderSystem = new RenderSystem(world, renderer, resourceManager, camera);
    }

    @Test
    void update_rendersAllSpriteEntities() {
        int e1 = 1; int e2 = 2;
        TransformComponent t1 = new TransformComponent();
        TransformComponent t2 = new TransformComponent();
        SpriteComponent s1 = new SpriteComponent("tex1");
        SpriteComponent s2 = new SpriteComponent("tex2");

        when(world.getEntitiesWith(TransformComponent.class, SpriteComponent.class)).thenReturn(List.of(e1, e2));
        when(world.getComponent(e1, TransformComponent.class)).thenReturn(t1);
        when(world.getComponent(e1, SpriteComponent.class)).thenReturn(s1);
        when(world.getComponent(e2, TransformComponent.class)).thenReturn(t2);
        when(world.getComponent(e2, SpriteComponent.class)).thenReturn(s2);
        when(resourceManager.resolveMeshHandle("quad")).thenReturn(quadMesh);
        when(resourceManager.resolveTextureHandle("tex1")).thenReturn(texture1);
        when(resourceManager.resolveTextureHandle("tex2")).thenReturn(texture2);

        renderSystem.update(0.016f);

        verify(renderer).beginScene(camera);
        verify(renderer).endScene();
        // Capture submit calls
        ArgumentCaptor<Matrix4f> matrixCaptor = ArgumentCaptor.forClass(Matrix4f.class);
        verify(renderer, times(2)).submit(eq(quadMesh), any(Texture.class), matrixCaptor.capture());
        // Two matrices captured
        assertEquals(2, matrixCaptor.getAllValues().size());
    }
}
