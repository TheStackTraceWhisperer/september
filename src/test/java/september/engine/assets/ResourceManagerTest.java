package september.engine.assets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.rendering.Mesh;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceManagerTest {

    @Mock
    private Texture mockTexture;
    @Mock
    private Shader mockShader;

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
    @DisplayName("loadTexture should cache the texture after the first load")
    void loadTexture_cachesResource() {
        // Act: Load the same texture twice using its handle.
        Texture texture1 = resourceManager.loadTexture("tex1", "path/to/tex");
        Texture texture2 = resourceManager.loadTexture("tex1", "path/to/tex");

        // Assert: The returned instances should be the same, and the loader should only be called once.
        assertSame(texture1, texture2, "Should return the same instance from cache.");
        assetLoader.verify(() -> AssetLoader.loadTexture(anyString()), times(1));
    }

    @Test
    @DisplayName("loadShader should cache the shader after the first load")
    void loadShader_cachesResource() {
        // Act: Load the same shader twice.
        Shader shader1 = resourceManager.loadShader("shader1", "v.glsl", "f.glsl");
        Shader shader2 = resourceManager.loadShader("shader1", "v.glsl", "f.glsl");

        // Assert: The instances should be the same, and the loader should only be called once.
        assertSame(shader1, shader2, "Should return the same instance from cache.");
        assetLoader.verify(() -> AssetLoader.loadShader(anyString(), anyString()), times(1));
    }

    @Test
    @DisplayName("loadProceduralMesh should create a new mesh on first load")
    void loadProceduralMesh_loadsMeshFirstTime() {
        try (MockedConstruction<Mesh> meshConstruction = mockConstruction(Mesh.class)) {
            // Act: Load a procedural mesh for the first time.
            resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});

            // Assert: A new mesh should have been constructed, and no old mesh should have been closed.
            assertEquals(1, meshConstruction.constructed().size());
            Mesh constructedMesh = meshConstruction.constructed().get(0);
            verify(constructedMesh, never()).close();
        }
    }

    @Test
    @DisplayName("loadProceduralMesh should replace and close an existing mesh with the same handle")
    void loadProceduralMesh_replacesAndClosesOldMesh() {
        try (MockedConstruction<Mesh> meshConstruction = mockConstruction(Mesh.class)) {
            // Arrange: Load an initial mesh.
            resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});
            Mesh oldMesh = meshConstruction.constructed().get(0);

            // Act: Load a new mesh with the same handle.
            resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});

            // Assert: The old mesh should be closed, and a total of two meshes should have been constructed.
            verify(oldMesh).close();
            assertEquals(2, meshConstruction.constructed().size());
        }
    }

    @Test
    @DisplayName("resolveHandle methods should return the correct loaded resources")
    void resolveMethods_returnCorrectResources() {
        // Arrange: Load one of each type of resource.
        resourceManager.loadTexture("tex1", "path/to/tex");
        resourceManager.loadShader("shader1", "v.glsl", "f.glsl");
        try (MockedConstruction<Mesh> meshConstruction = mockConstruction(Mesh.class)) {
            resourceManager.loadProceduralMesh("mesh1", new float[]{}, new int[]{});
            Mesh loadedMesh = meshConstruction.constructed().get(0);

            // Act & Assert: Verify that resolving each handle returns the correct object.
            assertSame(mockTexture, resourceManager.resolveTextureHandle("tex1"));
            assertSame(mockShader, resourceManager.resolveShaderHandle("shader1"));
            assertSame(loadedMesh, resourceManager.resolveMeshHandle("mesh1"));
        }
    }

    @Test
    @DisplayName("resolveHandle methods should throw NullPointerException for invalid handles")
    void resolveMethods_throwForInvalidHandles() {
        assertThrows(NullPointerException.class, () -> resourceManager.resolveTextureHandle("invalid"));
        assertThrows(NullPointerException.class, () -> resourceManager.resolveShaderHandle("invalid"));
        assertThrows(NullPointerException.class, () -> resourceManager.resolveMeshHandle("invalid"));
    }

    @Test
    @DisplayName("close() should close all managed resources and clear caches")
    void close_closesAllManagedResources() {
        try (MockedConstruction<Mesh> meshConstruction = mockConstruction(Mesh.class)) {
            // Arrange: Load one of each resource type.
            resourceManager.loadTexture("tex1", "path/to/tex");
            resourceManager.loadShader("shader1", "v.glsl", "f.glsl");
            resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});
            Mesh loadedMesh = meshConstruction.constructed().get(0);

            // Act: Close the resource manager.
            resourceManager.close();

            // Assert: Verify that close() was called on every managed resource.
            verify(mockTexture).close();
            verify(mockShader).close();
            verify(loadedMesh).close();

            // Assert: Verify that caches are cleared by trying to resolve handles again.
            assertThrows(NullPointerException.class, () -> resourceManager.resolveTextureHandle("tex1"));
            assertThrows(NullPointerException.class, () -> resourceManager.resolveShaderHandle("shader1"));
            assertThrows(NullPointerException.class, () -> resourceManager.resolveMeshHandle("procMesh"));
        }
    }
}
