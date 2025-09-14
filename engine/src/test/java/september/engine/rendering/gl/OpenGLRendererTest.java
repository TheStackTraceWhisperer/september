package september.engine.rendering.gl;

import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;
import september.engine.rendering.Texture;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Integration smoke test for the OpenGLRenderer.
 * This test verifies that the renderer can execute a full scene lifecycle with live GPU resources
 * without causing any errors.
 */
class OpenGLRendererTest extends EngineTestHarness {

    private Renderer renderer;

    @BeforeEach
    void setupRenderer() {
        // The harness provides a live OpenGL context.
        // We can now create a real renderer instance.
        renderer = new OpenGLRenderer();
    }

    @Test
    @DisplayName("Renderer should execute a full scene lifecycle without errors")
    void renderer_executesFullLifecycle_withoutError() {
        // --- Arrange ---
        // The harness provides a live camera and a resource manager.
        // The engine.init() call in the harness already loads the default "quad" mesh and textures.
        Mesh quadMesh = resourceManager.resolveMeshHandle("quad");
        Texture playerTexture = resourceManager.resolveTextureHandle("player_texture");
        Matrix4f transformMatrix = new Matrix4f().identity();

        // --- Act & Assert ---
        // This test passes if the entire rendering sequence completes without any exceptions,
        // which proves that the renderer can correctly orchestrate all the live GPU resources.
        assertThatCode(() -> {
            renderer.beginScene(camera);
            renderer.submit(quadMesh, playerTexture, transformMatrix);
            renderer.endScene();
        }).doesNotThrowAnyException();
    }
}
