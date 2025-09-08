package september.engine.assets;

import september.engine.rendering.Mesh;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the loading, caching, and lifecycle of engine assets.
 *
 * This class ensures that resources like meshes and textures are loaded only once
 * and provides a central point of access for retrieving them via a string handle.
 * It implements AutoCloseable to guarantee that all native resources it manages
 * are freed when the manager is closed.
 */
public final class ResourceManager implements AutoCloseable {

  private final Map<String, Mesh> meshCache;
  // In the future, you would add more caches:
  // private final Map<String, Texture> textureCache;
  // private final Map<String, Shader> shaderCache;

  public ResourceManager() {
    this.meshCache = new HashMap<>();
  }

  /**
   * Creates a new Mesh from raw vertex data and stores it under a given handle.
   * If a mesh with the same handle already exists, it will be overwritten.
   *
   * @param handle   The unique string identifier for this mesh.
   * @param vertices The vertex data (e.g., positions).
   * @param indices  The index data defining the triangles.
   * @return The newly created Mesh object.
   */
  public Mesh loadProceduralMesh(String handle, float[] vertices, int[] indices) {
    // In a real engine, you'd check if the handle exists and decide on a policy
    // (e.g., throw exception, log warning, or replace). For now, we'll replace.
    if (meshCache.containsKey(handle)) {
      meshCache.get(handle).close(); // Clean up the old mesh
    }

    Mesh mesh = new Mesh(vertices, indices);
    meshCache.put(handle, mesh);
    return mesh;
  }

  /**
   * Retrieves a previously loaded Mesh by its handle.
   *
   * @param handle The string identifier of the mesh to retrieve.
   * @return The Mesh object, or null if no mesh with that handle is found.
   */
  public Mesh resolveMeshHandle(String handle) {
    return meshCache.get(handle);
  }

  /**
   * Frees all managed resources. This iterates through all cached assets
   * and calls their respective close() methods to release native resources.
   */
  @Override
  public void close() {
    // Use removeIf to iterate and clear the map safely
    meshCache.values().forEach(Mesh::close);
    meshCache.clear();

    // textureCache.values().forEach(Texture::close);
    // textureCache.clear();
  }
}
