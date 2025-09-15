package september.engine.rendering;

import september.engine.rendering.gl.Shader;

/**
 * Represents a material that defines how a mesh should be rendered.
 * <p>
 * A material encapsulates the shader program, textures, and other rendering properties
 * that determine the visual appearance of a rendered object. This provides a higher-level
 * abstraction over individual rendering components.
 * <p>
 * This is a basic material system that is not yet integrated into the main rendering pipeline.
 */
public interface Material extends AutoCloseable {

  /**
   * Gets the shader program used by this material.
   *
   * @return The shader program for this material.
   */
  Shader getShader();

  /**
   * Gets the primary texture used by this material.
   * 
   * @return The texture for this material, or null if no texture is set.
   */
  Texture getTexture();

  /**
   * Binds this material for rendering. This typically involves binding the shader
   * and setting up any textures or uniforms.
   */
  void bind();

  /**
   * Unbinds this material after rendering.
   */
  void unbind();

  /**
   * Gets a human-readable name for this material.
   *
   * @return The name of this material.
   */
  String getName();
}