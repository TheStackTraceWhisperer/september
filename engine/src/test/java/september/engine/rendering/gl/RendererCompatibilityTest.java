package september.engine.rendering.gl;

import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Texture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OpenGL renderer implementations.
 * Tests that both OpenGLRenderer and InstancedOpenGLRenderer have compatible interfaces.
 */
class RendererCompatibilityTest {

  private Camera mockCamera;
  private Mesh mockMesh;
  private Texture mockTexture;
  private Matrix4f transform;

  @BeforeEach
  void setUp() {
    mockCamera = mock(Camera.class);
    mockMesh = mock(Mesh.class);
    mockTexture = mock(Texture.class);
    transform = new Matrix4f().identity();

    // Setup camera mocks
    when(mockCamera.getProjectionMatrix()).thenReturn(new Matrix4f().identity());
    when(mockCamera.getViewMatrix()).thenReturn(new Matrix4f().identity());
  }

  @Test
  @DisplayName("OpenGLRenderer should have required rendering methods")
  void openGLRenderer_hasRequiredMethods() {
    // Verify OpenGLRenderer has the core rendering methods
    try {
      var beginSceneMethod = OpenGLRenderer.class.getMethod("beginScene", Camera.class);
      var submitMethod = OpenGLRenderer.class.getMethod("submit", Mesh.class, Texture.class, Matrix4f.class);
      var endSceneMethod = OpenGLRenderer.class.getMethod("endScene");

      assertThat(beginSceneMethod).isNotNull();
      assertThat(submitMethod).isNotNull();
      assertThat(endSceneMethod).isNotNull();

      // Verify return types
      assertThat(beginSceneMethod.getReturnType()).isEqualTo(void.class);
      assertThat(submitMethod.getReturnType()).isEqualTo(void.class);
      assertThat(endSceneMethod.getReturnType()).isEqualTo(void.class);
    } catch (NoSuchMethodException e) {
      throw new AssertionError("OpenGLRenderer missing required rendering methods", e);
    }
  }

  @Test
  @DisplayName("InstancedOpenGLRenderer should have compatible interface with OpenGLRenderer")
  void instancedRenderer_hasCompatibleInterface() {
    // This test verifies that InstancedOpenGLRenderer has the same core methods as OpenGLRenderer

    // Verify the methods exist and have correct signatures
    try {
      var beginSceneMethod = InstancedOpenGLRenderer.class.getMethod("beginScene", Camera.class);
      var submitMethod = InstancedOpenGLRenderer.class.getMethod("submit", Mesh.class, Texture.class, Matrix4f.class);
      var endSceneMethod = InstancedOpenGLRenderer.class.getMethod("endScene");

      assertThat(beginSceneMethod).isNotNull();
      assertThat(submitMethod).isNotNull();
      assertThat(endSceneMethod).isNotNull();

      // Verify return types
      assertThat(beginSceneMethod.getReturnType()).isEqualTo(void.class);
      assertThat(submitMethod.getReturnType()).isEqualTo(void.class);
      assertThat(endSceneMethod.getReturnType()).isEqualTo(void.class);

    } catch (NoSuchMethodException e) {
      throw new AssertionError("InstancedOpenGLRenderer missing required rendering methods", e);
    }
  }

  @Test
  @DisplayName("InstancedOpenGLRenderer should provide additional statistics functionality")
  void instancedRenderer_providesAdditionalFunctionality() {
    // Verify that the instanced renderer has additional methods for monitoring performance
    try {
      var getStatsMethod = InstancedOpenGLRenderer.class.getMethod("getLastFrameStats");
      var getBatchMethod = InstancedOpenGLRenderer.class.getMethod("getSpriteBatch");

      assertThat(getStatsMethod).isNotNull();
      assertThat(getBatchMethod).isNotNull();

      // Check return types
      assertThat(getStatsMethod.getReturnType().getSimpleName()).isEqualTo("RenderStats");
      assertThat(getBatchMethod.getReturnType()).isEqualTo(september.engine.rendering.SpriteBatch.class);

    } catch (NoSuchMethodException e) {
      throw new AssertionError("InstancedOpenGLRenderer missing expected additional methods", e);
    }
  }
}
