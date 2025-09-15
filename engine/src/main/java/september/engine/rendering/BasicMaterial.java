package september.engine.rendering;

import september.engine.rendering.gl.Shader;

import java.util.Objects;

/**
 * A basic implementation of the Material interface that combines a shader and texture.
 * <p>
 * This material represents the most common rendering case: a textured mesh with a shader program.
 * It provides basic binding/unbinding functionality for use in a rendering pipeline.
 * <p>
 * This is a basic material system that is not yet integrated into the main rendering pipeline.
 */
public class BasicMaterial implements Material {

  private final String name;
  private final Shader shader;
  private final Texture texture;

  /**
   * Creates a new BasicMaterial with the specified components.
   *
   * @param name    A human-readable name for this material.
   * @param shader  The shader program to use for rendering.
   * @param texture The texture to apply, or null for untextured rendering.
   */
  public BasicMaterial(String name, Shader shader, Texture texture) {
    this.name = Objects.requireNonNull(name, "Material name cannot be null");
    this.shader = Objects.requireNonNull(shader, "Shader cannot be null");
    this.texture = texture; // texture can be null for untextured materials
  }

  @Override
  public Shader getShader() {
    return shader;
  }

  @Override
  public Texture getTexture() {
    return texture;
  }

  @Override
  public void bind() {
    shader.bind();
    if (texture != null) {
      texture.bind(0);
      shader.setUniform("uTextureSampler", 0);
    }
  }

  @Override
  public void unbind() {
    shader.unbind();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void close() {
    // Materials don't own their shader or texture resources,
    // so they don't need to close them. The ResourceManager
    // or other owner is responsible for cleanup.
  }

  @Override
  public String toString() {
    return "BasicMaterial{" +
      "name='" + name + '\'' +
      ", hasTexture=" + (texture != null) +
      '}';
  }
}