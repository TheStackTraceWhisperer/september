package september.engine.assets;

import september.engine.audio.AudioBuffer;
import september.engine.rendering.Mesh;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages the loading, caching, and lifecycle of engine assets.
 * <p>
 * This class ensures that resources like meshes and textures are loaded only once
 * and provides a central point of access for retrieving them via a string handle.
 * It implements AutoCloseable to guarantee that all native resources it manages
 * are freed when the manager is closed.
 */
public final class ResourceManager implements AutoCloseable {

  private final Map<String, Mesh> meshCache = new HashMap<>();
  private final Map<String, Texture> textureCache = new HashMap<>();
  private final Map<String, Shader> shaderCache = new HashMap<>();
  private final Map<String, AudioBuffer> audioBufferCache = new HashMap<>();

  /**
   * Loads a texture from a file, stores it in the cache, and returns it.
   * If the texture is already cached, returns the existing instance.
   *
   * @param handle   The unique handle for this texture.
   * @param filePath The classpath path to the image file.
   * @return The cached or newly loaded Texture.
   */
  public Texture loadTexture(String handle, String filePath) {
    return textureCache.computeIfAbsent(handle, h -> AssetLoader.loadTexture(filePath));
  }

  /**
   * Loads a shader program from two files, stores it, and returns it.
   * If the shader is already cached, returns the existing instance.
   *
   * @param handle       The unique handle for this shader.
   * @param vertexPath   The classpath path to the vertex shader file.
   * @param fragmentPath The classpath path to the fragment shader file.
   * @return The cached or newly loaded Shader.
   */
  public Shader loadShader(String handle, String vertexPath, String fragmentPath) {
    return shaderCache.computeIfAbsent(handle, h -> AssetLoader.loadShader(vertexPath, fragmentPath));
  }

  /**
   * Creates a new Mesh from raw vertex data and stores it under a given handle.
   * If a mesh with the same handle already exists, it will be closed and replaced.
   *
   * @param handle   The unique string identifier for this mesh.
   * @param vertices The vertex data (e.g., positions, UVs).
   * @param indices  The index data defining the triangles.
   */
  public void loadProceduralMesh(String handle, float[] vertices, int[] indices) {
    if (meshCache.containsKey(handle)) {
      meshCache.get(handle).close(); // Clean up the old mesh if it exists
    }
    meshCache.put(handle, new Mesh(vertices, indices));
  }

  public Mesh resolveMeshHandle(String handle) {
    Mesh mesh = meshCache.get(handle);
    Objects.requireNonNull(mesh, "Mesh not found: " + handle);
    return mesh;
  }

  public Texture resolveTextureHandle(String handle) {
    Texture texture = textureCache.get(handle);
    Objects.requireNonNull(texture, "Texture not found: " + handle);
    return texture;
  }

  /**
   * Loads an audio buffer from an OGG Vorbis file, stores it in the cache, and returns it.
   * If the audio buffer is already cached, returns the existing instance.
   *
   * @param handle   The unique handle for this audio buffer.
   * @param filePath The classpath path to the OGG file.
   * @return The cached or newly loaded AudioBuffer.
   */
  public AudioBuffer loadAudioBuffer(String handle, String filePath) {
    return audioBufferCache.computeIfAbsent(handle, h -> AudioBuffer.loadFromOggFile(filePath));
  }

  public Shader resolveShaderHandle(String handle) {
    Shader shader = shaderCache.get(handle);
    Objects.requireNonNull(shader, "Shader not found: " + handle);
    return shader;
  }

  public AudioBuffer resolveAudioBufferHandle(String handle) {
    AudioBuffer audioBuffer = audioBufferCache.get(handle);
    Objects.requireNonNull(audioBuffer, "AudioBuffer not found: " + handle);
    return audioBuffer;
  }

  /**
   * Frees all managed resources. This iterates through all cached assets
   * and calls their respective close() methods to release native resources.
   */
  @Override
  public void close() {
    meshCache.values().forEach(Mesh::close);
    meshCache.clear();

    textureCache.values().forEach(Texture::close);
    textureCache.clear();

    shaderCache.values().forEach(Shader::close);
    shaderCache.clear();

    audioBufferCache.values().forEach(AudioBuffer::close);
    audioBufferCache.clear();
  }
}

