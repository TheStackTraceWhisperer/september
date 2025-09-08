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
  private final int vertexShaderId;
  private final int fragmentShaderId;

  private final Map<String, Integer> uniforms = new HashMap<>();

  public Shader(String vertexSource, String fragmentSource) {
    vertexShaderId = createShader(vertexSource, GL_VERTEX_SHADER);
    fragmentShaderId = createShader(fragmentSource, GL_FRAGMENT_SHADER);

    programId = glCreateProgram();
    glAttachShader(programId, vertexShaderId);
    glAttachShader(programId, fragmentShaderId);
    glLinkProgram(programId);

    if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
      throw new RuntimeException("Error linking shader code: " + glGetProgramInfoLog(programId, 1024));
    }

    // Detach shaders after a successful link
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

  public void setUniform(String name, Matrix4f value) {
    if (!uniforms.containsKey(name)) {
      uniforms.put(name, glGetUniformLocation(programId, name));
    }
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer fb = stack.mallocFloat(16);
      value.get(fb);
      glUniformMatrix4fv(uniforms.get(name), false, fb);
    }
  }

  private int createShader(String shaderSource, int shaderType) {
    int shaderId = glCreateShader(shaderType);
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
