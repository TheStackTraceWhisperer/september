package september.engine.rendering.gl;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Manages an OpenGL shader program, including compiling, linking, and setting uniforms.
 */
public class Shader implements AutoCloseable {
  private final int programId;
  // Individual shader IDs are no longer needed as fields after linking
  private final Map<String, Integer> uniforms = new HashMap<>();

  public Shader(String vertexSource, String fragmentSource) {
    int vertexShaderId = createShader(vertexSource, GL_VERTEX_SHADER);
    int fragmentShaderId = createShader(fragmentSource, GL_FRAGMENT_SHADER);

    programId = glCreateProgram();
    glAttachShader(programId, vertexShaderId);
    glAttachShader(programId, fragmentShaderId);
    glLinkProgram(programId);

    if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
      throw new RuntimeException("Error linking shader code: " + glGetProgramInfoLog(programId, 1024));
    }

    // Best Practice: Detach and delete the individual shaders after a successful link
    // as they are no longer needed.
    glDetachShader(programId, vertexShaderId);
    glDetachShader(programId, fragmentShaderId);
    glDeleteShader(vertexShaderId);
    glDeleteShader(fragmentShaderId);
  }

  public void bind() {
    glUseProgram(programId);
  }

  public void unbind() {
    glUseProgram(0);
  }

  /**
   * Caches and sets a Matrix4f uniform.
   * @param name The name of the uniform in the shader code.
   * @param value The Matrix4f value to set.
   */
  public void setUniform(String name, Matrix4f value) {
    int location = getUniformLocation(name);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer fb = stack.mallocFloat(16);
      value.get(fb);
      glUniformMatrix4fv(location, false, fb);
    }
  }

  /**
   * Caches and sets an integer uniform. This is essential for setting texture samplers.
   * @param name The name of the uniform in the shader code (e.g., "uTextureSampler").
   * @param value The integer value to set (e.g., 0 for texture unit GL_TEXTURE0).
   */
  public void setUniform(String name, int value) {
    int location = getUniformLocation(name);
    glUniform1i(location, value);
  }

  private int getUniformLocation(String name) {
    // Memoization: Look up the location once and cache it for future frames.
    return uniforms.computeIfAbsent(name, n -> glGetUniformLocation(programId, n));
  }

  private int createShader(String shaderSource, int shaderType) {
    int shaderId = glCreateShader(shaderType);
    if (shaderId == 0) {
      throw new RuntimeException("Error creating shader of type " + shaderType);
    }
    glShaderSource(shaderId, shaderSource);
    glCompileShader(shaderId);

    if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
      throw new RuntimeException("Error compiling shader code: " + glGetShaderInfoLog(shaderId, 1024));
    }
    return shaderId;
  }

  @Override
  public void close() {
    unbind();
    if (programId != 0) {
      glDeleteProgram(programId);
    }
  }
}
