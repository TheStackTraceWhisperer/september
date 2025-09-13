package september.engine.rendering.gl;

/**
 * Shader sources for instanced rendering.
 * <p>
 * These shaders support rendering multiple instances of the same mesh with different
 * transformation matrices using OpenGL instancing features.
 */
public final class InstancedShaderSources {

  /**
   * Vertex shader for instanced sprite rendering.
   * Uses instance attributes for per-instance transformation matrices.
   */
  public static final String INSTANCED_VERTEX_SHADER =
    "#version 460 core\n" +
    "\n" +
    "// Per-vertex attributes\n" +
    "layout (location = 0) in vec3 aPos;\n" +
    "layout (location = 1) in vec2 aTexCoord;\n" +
    "\n" +
    "// Per-instance attributes (transformation matrix)\n" +
    "layout (location = 2) in vec4 aInstanceMatrix0;\n" +
    "layout (location = 3) in vec4 aInstanceMatrix1;\n" +
    "layout (location = 4) in vec4 aInstanceMatrix2;\n" +
    "layout (location = 5) in vec4 aInstanceMatrix3;\n" +
    "\n" +
    "// Outputs to fragment shader\n" +
    "out vec2 vTexCoord;\n" +
    "\n" +
    "// Scene uniforms\n" +
    "uniform mat4 uProjection;\n" +
    "uniform mat4 uView;\n" +
    "\n" +
    "void main()\n" +
    "{\n" +
    "    // Reconstruct instance transformation matrix from vec4 attributes\n" +
    "    mat4 instanceMatrix = mat4(\n" +
    "        aInstanceMatrix0,\n" +
    "        aInstanceMatrix1,\n" +
    "        aInstanceMatrix2,\n" +
    "        aInstanceMatrix3\n" +
    "    );\n" +
    "    \n" +
    "    // Transform vertex position using instance matrix\n" +
    "    gl_Position = uProjection * uView * instanceMatrix * vec4(aPos, 1.0);\n" +
    "    \n" +
    "    // Pass through texture coordinates unchanged\n" +
    "    vTexCoord = aTexCoord;\n" +
    "}";

  /**
   * Fragment shader for instanced sprite rendering.
   * Same as regular sprite fragment shader since instancing only affects vertex processing.
   */
  public static final String INSTANCED_FRAGMENT_SHADER =
    "#version 460 core\n" +
    "\n" +
    "// Inputs from vertex shader\n" +
    "in vec2 vTexCoord;\n" +
    "\n" +
    "// Output color\n" +
    "out vec4 FragColor;\n" +
    "\n" +
    "// Texture sampler\n" +
    "uniform sampler2D uTextureSampler;\n" +
    "\n" +
    "void main()\n" +
    "{\n" +
    "    FragColor = texture(uTextureSampler, vTexCoord);\n" +
    "}";

  // Private constructor to prevent instantiation
  private InstancedShaderSources() {
    throw new UnsupportedOperationException("Utility class");
  }
}