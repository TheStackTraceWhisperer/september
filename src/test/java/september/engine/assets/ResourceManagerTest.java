package september.engine.assets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.rendering.Mesh;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceManagerTest {

    @Mock
    private Texture mockTexture;
    @Mock
    private Shader mockShader;
    @Mock
    private Mesh mockMesh;

    private ResourceManager resourceManager;
    private MockedStatic<AssetLoader> assetLoader;

    @BeforeEach
    void setUp() {
        resourceManager = new ResourceManager();
        assetLoader = mockStatic(AssetLoader.class);

        // Mock the static AssetLoader methods to return our mock objects
        assetLoader.when(() -> AssetLoader.loadTexture(anyString())).thenReturn(mockTexture);
        assetLoader.when(() -> AssetLoader.loadShader(anyString(), anyString())).thenReturn(mockShader);
    }

    @AfterEach
    void tearDown() {
        assetLoader.close();
    }

    @Test
    void loadTexture_cachesResource() {
        // Act
        Texture texture1 = resourceManager.loadTexture("tex1", "path/to/tex");
        Texture texture2 = resourceManager.loadTexture("tex1", "path/to/tex");

        // Assert
        assertSame(texture1, texture2, "Should return the same instance from cache.");
        assetLoader.verify(() -> AssetLoader.loadTexture(anyString()), times(1));
    }

    @Test
    void loadShader_cachesResource() {
        // Act
        Shader shader1 = resourceManager.loadShader("shader1", "v.glsl", "f.glsl");
        Shader shader2 = resourceManager.loadShader("shader1", "v.glsl", "f.glsl");

        // Assert
        assertSame(shader1, shader2, "Should return the same instance from cache.");
        assetLoader.verify(() -> AssetLoader.loadShader(anyString(), anyString()), times(1));
    }

    @Test
    void loadProceduralMesh_replacesAndClosesOldMesh() {
        try (MockedConstruction<Mesh> meshConstruction = mockConstruction(Mesh.class)) {
            // Arrange: Load an initial mesh
            resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});
            Mesh oldMesh = (Mesh) meshConstruction.constructed().get(0);

            // Act: Load a new mesh with the same handle
            resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});

            // Assert
            verify(oldMesh).close();
            assertEquals(2, meshConstruction.constructed().size());
        }
    }

    @Test
    void resolveMethods_returnCorrectResources() {
        // Arrange
        resourceManager.loadTexture("tex1", "path/to/tex");
        resourceManager.loadShader("shader1", "v.glsl", "f.glsl");

        // Act & Assert
        assertSame(mockTexture, resourceManager.resolveTextureHandle("tex1"));
        assertSame(mockShader, resourceManager.resolveShaderHandle("shader1"));
    }

    @Test
    void resolveMethods_throwForInvalidHandles() {
        assertThrows(NullPointerException.class, () -> resourceManager.resolveTextureHandle("invalid"));
        assertThrows(NullPointerException.class, () -> resourceManager.resolveShaderHandle("invalid"));
        assertThrows(NullPointerException.class, () -> resourceManager.resolveMeshHandle("invalid"));
    }

    @Test
    void close_closesAllManagedResources() {
        // Arrange
        resourceManager.loadTexture("tex1", "path/to/tex");
        resourceManager.loadShader("shader1", "v.glsl", "f.glsl");
        try (MockedConstruction<Mesh> meshConstruction = mockConstruction(Mesh.class, (mock, context) -> {
            // Load a mesh into the cache
            resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});

            // Act
            resourceManager.close();

            // Assert
            verify(mockTexture).close();
            verify(mockShader).close();
            verify(mock).close(); // Verify the procedurally loaded mesh is closed
        })) {}
    }
}
