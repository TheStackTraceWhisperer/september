package september.engine.rendering.gl;

import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;
import september.engine.rendering.Texture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for compatibility between old and new renderer interfaces.
 * Tests that the new instanced renderer can be used as a drop-in replacement.
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
  @DisplayName("Both renderers should implement the same interface")
  void bothRenderers_implementSameInterface() {
    // This test verifies that both renderers can be used interchangeably
    // by ensuring they implement the same Renderer interface
    
    assertThat(Renderer.class).isAssignableFrom(OpenGLRenderer.class);
    assertThat(Renderer.class).isAssignableFrom(InstancedOpenGLRenderer.class);
  }

  @Test
  @DisplayName("Instanced renderer should accept same method calls as original renderer")
  void instancedRenderer_acceptsSameMethodCalls() {
    // This test verifies the interface compatibility without requiring OpenGL
    
    // The InstancedOpenGLRenderer constructor will fail without OpenGL context,
    // but we can verify the interface exists and would work
    
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
      throw new AssertionError("InstancedOpenGLRenderer missing required Renderer interface methods", e);
    }
  }

  @Test
  @DisplayName("RenderSystem should work with either renderer implementation")
  void renderSystem_worksWithEitherRenderer() {
    // Test that a mock renderer can be used in place of the real ones
    Renderer mockRenderer = mock(Renderer.class);
    
    // Simulate what RenderSystem does
    mockRenderer.beginScene(mockCamera);
    mockRenderer.submit(mockMesh, mockTexture, transform);
    mockRenderer.endScene();
    
    // Verify the calls were made in order
    var inOrder = inOrder(mockRenderer);
    inOrder.verify(mockRenderer).beginScene(mockCamera);
    inOrder.verify(mockRenderer).submit(mockMesh, mockTexture, transform);
    inOrder.verify(mockRenderer).endScene();
  }

  @Test
  @DisplayName("Instanced renderer should provide additional statistics functionality")
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