package september;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.assets.AssetLoader;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * This test provides definitive proof that the GLSL compiler supports version 4.60.
 */
public class Glsl460FeatureTest extends EngineTestHarness {

    @Test
    @DisplayName("GLSL compiler should support version 4.60 features (e.g., gl_BaseVertex)")
    void shaderCompiler_supportsGlsl460Features() {
        // Arrange: The harness provides a live GL context.
        // We will attempt to load a shader that uses a feature only available in GLSL 4.60.
        // This test's success proves that the compiler in our environment is truly 4.60-compliant,
        // even with the MESA_GL_VERSION_OVERRIDE in place.

        // Act & Assert
        assertThatCode(() -> {
            // Use the new, compatible shader pair.
            AssetLoader.loadShader("shaders/test_glsl_460.vert", "shaders/test_glsl_460.frag");
        }).as("Loading a shader with GLSL 4.60 features should succeed.")
          .doesNotThrowAnyException();
    }
}
