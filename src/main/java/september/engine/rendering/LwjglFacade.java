package september.engine.rendering;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.Callback;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * A 1:1 facade for LWJGL static calls to allow for easier mocking and testing.
 */
public interface LwjglFacade {

    // GLFW
    boolean glfwInit();
    void glfwTerminate();
    void glfwDefaultWindowHints();
    void glfwWindowHint(int hint, int value);
    long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share);
    void glfwMakeContextCurrent(long window);
    void glfwSwapInterval(int interval);
    void glfwShowWindow(long window);
    void glfwSwapBuffers(long window);
    void glfwPollEvents();
    boolean glfwWindowShouldClose(long window);
    void glfwDestroyWindow(long window);
    long glfwGetCurrentContext();
    GLFWErrorCallback glfwSetErrorCallback(GLFWErrorCallback callback);
    GLFWKeyCallback glfwSetKeyCallback(long window, GLFWKeyCallback callback);
    GLFWMouseButtonCallback glfwSetMouseButtonCallback(long window, GLFWMouseButtonCallback callback);
    GLFWCursorPosCallback glfwSetCursorPosCallback(long window, GLFWCursorPosCallback callback);
    GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(long window, GLFWFramebufferSizeCallback callback);

    // GL
    GLCapabilities createCapabilities();
    GLCapabilities getCapabilities();

    // GL11
    String glGetString(int name);
    int glGetError();
    void glEnable(int cap);
    void glClearColor(float red, float green, float blue, float alpha);
    void glClear(int mask);
    void glDrawElements(int mode, int count, int type, long indices);
    void glBindTexture(int target, int texture);
    void glGenTextures(IntBuffer textures);
    void glTexParameteri(int target, int pname, int param);
    void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels);

    // GL13
    void glActiveTexture(int texture);

    // GL20
    int glCreateShader(int type);
    void glShaderSource(int shader, CharSequence string);
    void glCompileShader(int shader);
    int glGetShaderi(int shader, int pname);
    String glGetShaderInfoLog(int shader, int maxLength);
    void glAttachShader(int program, int shader);
    void glLinkProgram(int program);
    int glGetProgrami(int program, int pname);
    String glGetProgramInfoLog(int program, int maxLength);
    void glDeleteShader(int shader);
    int glCreateProgram();
    void glUseProgram(int program);
    void glDeleteProgram(int program);
    int glGetUniformLocation(int program, CharSequence name);
    void glUniform1i(int location, int v0);
    void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value);

    // GL30
    int glGenVertexArrays();
    void glBindVertexArray(int array);
    void glGenerateMipmap(int target);
    void glDeleteVertexArrays(IntBuffer arrays);

    // GL43
    void glDebugMessageControl(int source, int type, int severity, IntBuffer ids, boolean enabled);

    // GLUtil
    Callback setupDebugMessageCallback(PrintStream stream);
}
