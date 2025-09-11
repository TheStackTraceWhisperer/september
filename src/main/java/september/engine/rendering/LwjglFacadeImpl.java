package september.engine.rendering;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Concrete implementation of the LwjglFacade, delegating to the static LWJGL methods.
 */
public class LwjglFacadeImpl implements LwjglFacade {

    // GLFW
    @Override
    public boolean glfwInit() {
        return GLFW.glfwInit();
    }

    @Override
    public void glfwTerminate() {
        GLFW.glfwTerminate();
    }

    @Override
    public void glfwDefaultWindowHints() {
        GLFW.glfwDefaultWindowHints();
    }

    @Override
    public void glfwWindowHint(int hint, int value) {
        GLFW.glfwWindowHint(hint, value);
    }

    @Override
    public long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
        return GLFW.glfwCreateWindow(width, height, title, monitor, share);
    }

    @Override
    public void glfwMakeContextCurrent(long window) {
        GLFW.glfwMakeContextCurrent(window);
    }

    @Override
    public void glfwSwapInterval(int interval) {
        GLFW.glfwSwapInterval(interval);
    }

    @Override
    public void glfwShowWindow(long window) {
        GLFW.glfwShowWindow(window);
    }

    @Override
    public void glfwSwapBuffers(long window) {
        GLFW.glfwSwapBuffers(window);
    }

    @Override
    public void glfwPollEvents() {
        GLFW.glfwPollEvents();
    }

    @Override
    public boolean glfwWindowShouldClose(long window) {
        return GLFW.glfwWindowShouldClose(window);
    }

    @Override
    public void glfwDestroyWindow(long window) {
        GLFW.glfwDestroyWindow(window);
    }

    @Override
    public long glfwGetCurrentContext() {
        return GLFW.glfwGetCurrentContext();
    }

    @Override
    public GLFWErrorCallback glfwSetErrorCallback(GLFWErrorCallback callback) {
        return GLFW.glfwSetErrorCallback(callback);
    }

    @Override
    public GLFWKeyCallback glfwSetKeyCallback(long window, GLFWKeyCallback callback) {
        return GLFW.glfwSetKeyCallback(window, callback);
    }

    @Override
    public GLFWMouseButtonCallback glfwSetMouseButtonCallback(long window, GLFWMouseButtonCallback callback) {
        return GLFW.glfwSetMouseButtonCallback(window, callback);
    }

    @Override
    public GLFWCursorPosCallback glfwSetCursorPosCallback(long window, GLFWCursorPosCallback callback) {
        return GLFW.glfwSetCursorPosCallback(window, callback);
    }

    @Override
    public GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(long window, GLFWFramebufferSizeCallback callback) {
        return GLFW.glfwSetFramebufferSizeCallback(window, callback);
    }

    // GL
    @Override
    public GLCapabilities createCapabilities() {
        return GL.createCapabilities();
    }

    @Override
    public GLCapabilities getCapabilities() {
        return GL.getCapabilities();
    }

    // GL11
    @Override
    public String glGetString(int name) {
        return GL11.glGetString(name);
    }

    @Override
    public int glGetError() {
        return GL11.glGetError();
    }

    @Override
    public void glEnable(int cap) {
        GL11.glEnable(cap);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        GL11.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void glClear(int mask) {
        GL11.glClear(mask);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, long indices) {
        GL11.glDrawElements(mode, count, type, indices);
    }

    @Override
    public void glBindTexture(int target, int texture) {
        GL11.glBindTexture(target, texture);
    }

    @Override
    public void glGenTextures(IntBuffer textures) {
        GL11.glGenTextures(textures);
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        GL11.glTexParameteri(target, pname, param);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
    }

    // GL13
    @Override
    public void glActiveTexture(int texture) {
        GL13.glActiveTexture(texture);
    }

    // GL20
    @Override
    public int glCreateShader(int type) {
        return GL20.glCreateShader(type);
    }

    @Override
    public void glShaderSource(int shader, CharSequence string) {
        GL20.glShaderSource(shader, string);
    }

    @Override
    public void glCompileShader(int shader) {
        GL20.glCompileShader(shader);
    }

    @Override
    public int glGetShaderi(int shader, int pname) {
        return GL20.glGetShaderi(shader, pname);
    }

    @Override
    public String glGetShaderInfoLog(int shader, int maxLength) {
        return GL20.glGetShaderInfoLog(shader, maxLength);
    }

    @Override
    public void glAttachShader(int program, int shader) {
        GL20.glAttachShader(program, shader);
    }

    @Override
    public void glLinkProgram(int program) {
        GL20.glLinkProgram(program);
    }

    @Override
    public int glGetProgrami(int program, int pname) {
        return GL20.glGetProgrami(program, pname);
    }

    @Override
    public String glGetProgramInfoLog(int program, int maxLength) {
        return GL20.glGetProgramInfoLog(program, maxLength);
    }

    @Override
    public void glDeleteShader(int shader) {
        GL20.glDeleteShader(shader);
    }

    @Override
    public int glCreateProgram() {
        return GL20.glCreateProgram();
    }

    @Override
    public void glUseProgram(int program) {
        GL20.glUseProgram(program);
    }

    @Override
    public void glDeleteProgram(int program) {
        GL20.glDeleteProgram(program);
    }

    @Override
    public int glGetUniformLocation(int program, CharSequence name) {
        return GL20.glGetUniformLocation(program, name);
    }

    @Override
    public void glUniform1i(int location, int v0) {
        GL20.glUniform1i(location, v0);
    }

    @Override
    public void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value) {
        GL20.glUniformMatrix4fv(location, transpose, value);
    }

    // GL30
    @Override
    public int glGenVertexArrays() {
        return GL30.glGenVertexArrays();
    }

    @Override
    public void glBindVertexArray(int array) {
        GL30.glBindVertexArray(array);
    }

    @Override
    public void glGenerateMipmap(int target) {
        GL30.glGenerateMipmap(target);
    }

    @Override
    public void glDeleteVertexArrays(IntBuffer arrays) {
        GL30.glDeleteVertexArrays(arrays);
    }

    // GL43
    @Override
    public void glDebugMessageControl(int source, int type, int severity, IntBuffer ids, boolean enabled) {
        GL43.glDebugMessageControl(source, type, severity, ids, enabled);
    }

    // GLUtil
    @Override
    public Callback setupDebugMessageCallback(PrintStream stream) {
        return GLUtil.setupDebugMessageCallback(stream);
    }
}
