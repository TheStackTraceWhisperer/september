package september.engine.ui.rendering;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import september.engine.assets.ResourceManager;
import september.engine.rendering.Camera;
import september.engine.rendering.InstancedMesh;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.InstancedShaderSources;
import september.engine.rendering.gl.Shader;
import september.engine.ui.components.UIButtonComponent;
import september.engine.ui.components.UIImageComponent;
import september.engine.ui.components.UITransformComponent;

public class UIRenderer {

  private record Sprite(Texture texture, Matrix4f transform) {}

  private final Camera uiCamera;
  private final Shader uiShader;
  private final ResourceManager resourceManager;
  private final InstancedMesh quadMesh;
  private final List<Sprite> spriteList;

  public UIRenderer(ResourceManager resourceManager, float screenWidth, float screenHeight) {
    this.resourceManager = resourceManager;
    this.uiCamera = new Camera();
    this.uiCamera
        .getProjectionMatrix()
        .identity()
        .ortho(0.0f, screenWidth, 0.0f, screenHeight, -1.0f, 1.0f);
    this.uiShader =
        new Shader(
            InstancedShaderSources.INSTANCED_VERTEX_SHADER,
            InstancedShaderSources.INSTANCED_FRAGMENT_SHADER);
    this.spriteList = new ArrayList<>();

    float[] vertices = {
      0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
      -0.5f, 0.5f, 0.0f, 0.0f, 1.0f
    };
    int[] indices = {0, 1, 3, 1, 2, 3};
    this.quadMesh = new InstancedMesh(vertices, indices);
  }

  public void begin() {
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    spriteList.clear();
    uiShader.bind();
    uiShader.setUniform("uProjection", uiCamera.getProjectionMatrix());
    uiShader.setUniform("uView", new Matrix4f().identity());
  }

  public void submit(UITransformComponent transform, UIImageComponent image) {
    if (image.textureHandle == null) {
      return;
    }
    Texture texture = resourceManager.resolveTextureHandle(image.textureHandle);
    Matrix4f modelMatrix = calculateModelMatrix(transform);
    spriteList.add(new Sprite(texture, modelMatrix));
  }

  public void submit(UITransformComponent transform, UIButtonComponent button) {
    String textureHandle =
        switch (button.currentState) {
          case HOVERED -> button.hoveredTexture;
          case PRESSED -> button.pressedTexture;
          default -> button.normalTexture;
        };

    if (textureHandle != null) {
      Texture texture = resourceManager.resolveTextureHandle(textureHandle);
      Matrix4f modelMatrix = calculateModelMatrix(transform);
      spriteList.add(new Sprite(texture, modelMatrix));
    }
  }

  public void end() {
    for (var sprite : spriteList) {
      sprite.texture.bind(0);
      uiShader.setUniform("uTextureSampler", 0);
      quadMesh.renderInstanced(List.of(sprite.transform));
    }
    uiShader.unbind();
    glDisable(GL_BLEND);
  }

  public void resize(float width, float height) {
    uiCamera.getProjectionMatrix().identity().ortho(0.0f, width, 0.0f, height, -1.0f, 1.0f);
  }

  private Matrix4f calculateModelMatrix(UITransformComponent transform) {
    float[] bounds = transform.screenBounds;
    float width = bounds[2] - bounds[0];
    float height = bounds[3] - bounds[1];
    float posX = bounds[0] + width / 2.0f;
    float posY = bounds[1] + height / 2.0f;
    return new Matrix4f().translate(posX, posY, transform.offset.z).scale(width, height, 1.0f);
  }

  public void close() {
    quadMesh.close();
    uiShader.close();
  }
}
