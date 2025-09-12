package september.engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CameraTest {

    private Camera camera;

    @BeforeEach
    void setUp() {
        // Defaults to a 16:9 orthographic projection
        camera = new Camera(16f, 9f);
    }

    @Test
    @DisplayName("getProjectionMatrix should default to a centered 16:9 orthographic projection")
    void getProjectionMatrix_defaultsToCenteredOrthographic169() {
        Matrix4f expected = new Matrix4f().identity().ortho(-8f, 8f, -4.5f, 4.5f, -1.0f, 100.0f);
        assertThat(camera.getProjectionMatrix()).isEqualTo(expected);
    }

    @Test
    @DisplayName("setOrthographic should update the projection matrix")
    void setOrthographic_updatesProjectionMatrix() {
        // Set a new projection, e.g., 20x10
        camera.setOrthographic(20f, 10f);
        Matrix4f expected = new Matrix4f().identity().ortho(-10f, 10f, -5f, 5f, -1.0f, 100.0f);
        assertThat(camera.getProjectionMatrix()).isEqualTo(expected);
    }

    @Test
    @DisplayName("setPosition should update the view matrix")
    void setPosition_affectsViewMatrix() {
        Vector3f newPos = new Vector3f(5f, 2f, 0f);
        camera.setPosition(newPos);
        // The view matrix should look from the new position towards its 'front' vector
        Matrix4f expectedView = new Matrix4f().lookAt(5f, 2f, 0f, 5f, 2f, -1f, 0f, 1f, 0f);
        assertThat(camera.getViewMatrix()).isEqualTo(expectedView);
    }

    @Test
    @DisplayName("resize with a wider aspect ratio should expand width to pillarbox")
    void resize_withWiderAspectRatio_expandsWidthToPillarbox() {
        // Initial state is 16:9. Resize to a wider 2:1 aspect ratio.
        // The height should remain constrained to the original world height (9), and the width should expand.
        camera.resize(2000, 1000); // 2:1 aspect ratio

        float originalOrthoHeight = 9f;
        float newAspectRatio = 2.0f;
        float scaledWidth = originalOrthoHeight * newAspectRatio; // 9 * 2 = 18

        Matrix4f expected = new Matrix4f().identity().ortho(-scaledWidth / 2f, scaledWidth / 2f, -originalOrthoHeight / 2f, originalOrthoHeight / 2f, -1.0f, 100.0f);
        assertThat(camera.getProjectionMatrix()).isEqualTo(expected);
    }

    @Test
    @DisplayName("resize with a taller aspect ratio should expand height to letterbox")
    void resize_withTallerAspectRatio_expandsHeightToLetterbox() {
        // Initial state is 16:9. Resize to a taller 1:1 aspect ratio.
        // The width should remain constrained to the original world width (16), and the height should expand.
        camera.resize(1000, 1000); // 1:1 aspect ratio

        float originalOrthoWidth = 16f;
        float newAspectRatio = 1.0f;
        float scaledHeight = originalOrthoWidth / newAspectRatio; // 16 / 1 = 16

        Matrix4f expected = new Matrix4f().identity().ortho(-originalOrthoWidth / 2f, originalOrthoWidth / 2f, -scaledHeight / 2f, scaledHeight / 2f, -1.0f, 100.0f);
        assertThat(camera.getProjectionMatrix()).isEqualTo(expected);
    }
}
