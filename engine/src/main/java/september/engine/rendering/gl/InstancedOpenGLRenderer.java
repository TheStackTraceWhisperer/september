package september.engine.rendering.gl;

import org.joml.Matrix4f;
import september.engine.rendering.Camera;
import september.engine.rendering.InstancedMesh;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;
import september.engine.rendering.SpriteBatch;
import september.engine.rendering.Texture;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * An OpenGL renderer that uses instanced rendering for improved performance.
 * <p>
 * This renderer batches sprites by texture and renders them using OpenGL instancing,
 * reducing the number of draw calls from one-per-sprite to one-per-texture.
 * It works alongside the existing OpenGLRenderer and uses the same interface.
 */
public final class InstancedOpenGLRenderer implements Renderer {

  private final Shader instancedShader;
  private final SpriteBatch spriteBatch;
  private InstancedMesh quadMesh;

  public InstancedOpenGLRenderer() {
    // Create the instanced shader program
    this.instancedShader = new Shader(
      InstancedShaderSources.INSTANCED_VERTEX_SHADER,
      InstancedShaderSources.INSTANCED_FRAGMENT_SHADER
    );
    this.spriteBatch = new SpriteBatch();
  }

  /**
   * Sets the quad mesh to use for all sprite rendering.
   * This should be called once during initialization.
   *
   * @param quadMesh The instanced mesh for sprite quads.
   */
  public void setQuadMesh(InstancedMesh quadMesh) {
    this.quadMesh = quadMesh;
  }

  @Override
  public void beginScene(Camera camera) {
    // Clear the screen
    glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Clear the sprite batch for this frame
    spriteBatch.clear();

    // Prepare the shader for the scene
    instancedShader.bind();
    instancedShader.setUniform("uProjection", camera.getProjectionMatrix());
    instancedShader.setUniform("uView", camera.getViewMatrix());
  }

  @Override
  public void submit(Mesh mesh, Texture texture, Matrix4f transform) {
    // Add sprite to batch instead of rendering immediately
    spriteBatch.addSprite(texture, new Matrix4f(transform));
  }

  @Override
  public void endScene() {
    if (quadMesh == null) {
      throw new IllegalStateException("QuadMesh must be set before rendering. Call setQuadMesh() during initialization.");
    }

    // Render all batches
    for (Texture texture : spriteBatch.getTextures()) {
      var transforms = spriteBatch.getSpritesForTexture(texture);

      if (!transforms.isEmpty()) {
        // Bind texture
        texture.bind(0);
        instancedShader.setUniform("uTextureSampler", 0);

        // Render all instances for this texture
        quadMesh.renderInstanced(transforms);
      }
    }

    // Unbind shader
    instancedShader.unbind();
  }

  /**
   * Gets rendering statistics for the current frame.
   *
   * @return A statistics object with batch and sprite counts.
   */
  public RenderStats getLastFrameStats() {
    return new RenderStats(
      spriteBatch.getBatchCount(),
      spriteBatch.getTotalSpriteCount()
    );
  }

  /**
   * Gets the sprite batch used by this renderer.
   * Useful for debugging or advanced batching scenarios.
   *
   * @return The current sprite batch.
   */
  public SpriteBatch getSpriteBatch() {
    return spriteBatch;
  }

  /**
   * Simple statistics class for rendering performance metrics.
   */
  public static class RenderStats {
    public final int batchCount;
    public final int spriteCount;

    public RenderStats(int batchCount, int spriteCount) {
      this.batchCount = batchCount;
      this.spriteCount = spriteCount;
    }

    @Override
    public String toString() {
      return String.format("RenderStats{batches=%d, sprites=%d}", batchCount, spriteCount);
    }
  }
}
