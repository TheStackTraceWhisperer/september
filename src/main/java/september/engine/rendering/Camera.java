package september.engine.rendering;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents a camera in the game world, responsible for the view and projection matrices.
 * This camera can operate in either a 2D (Orthographic) or 3D (Perspective) mode.
 */
public class Camera {

  private enum ProjectionType {
    ORTHOGRAPHIC,
    PERSPECTIVE
  }

  @Getter
  private final Matrix4f projectionMatrix;
  private final Matrix4f viewMatrix;
  private final Vector3f position;
  private final Vector3f front;
  private final Vector3f up;

  // State for recalculating projection on resize
  private ProjectionType projectionType;
  private float orthoWidth, orthoHeight;
  private float fov, nearPlane, farPlane;

  private boolean viewDirty = true;

  /**
   * Creates a new camera with a default 2D orthographic projection (16 units wide, 9 units tall).
   */
  public Camera() {
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
    this.position = new Vector3f(0.0f, 0.0f, 0.0f);
    this.front = new Vector3f(0.0f, 0.0f, -1.0f);
    this.up = new Vector3f(0.0f, 1.0f, 0.0f);
    setOrthographic(16, 9); // Default to a 16:9 2D world view
  }

  /**
   * Creates a new camera with a specific 2D orthographic projection.
   * @param worldWidth The visible width of the game world.
   * @param worldHeight The visible height of the game world.
   */
  public Camera(float worldWidth, float worldHeight) {
    this(); // Call the default constructor to initialize fields
    setOrthographic(worldWidth, worldHeight);
  }

  private void calculateViewMatrix() {
    Vector3f target = new Vector3f();
    position.add(front, target);
    viewMatrix.identity().lookAt(position, target, up);
    viewDirty = false;
  }

  public Matrix4f getViewMatrix() {
    if (viewDirty) {
      calculateViewMatrix();
    }
    return viewMatrix;
  }

  public void setPosition(Vector3f position) {
    this.position.set(position);
    this.viewDirty = true;
  }

  /**
   * Configures the camera for 2D rendering with an orthographic projection.
   * The view will be centered at the origin.
   *
   * @param worldWidth  The desired visible width of the world.
   * @param worldHeight The desired visible height of the world.
   */
  public final void setOrthographic(float worldWidth, float worldHeight) {
    this.projectionType = ProjectionType.ORTHOGRAPHIC;
    this.orthoWidth = worldWidth;
    this.orthoHeight = worldHeight;
    float halfW = worldWidth / 2.0f;
    float halfH = worldHeight / 2.0f;
    projectionMatrix.identity().ortho(-halfW, halfW, -halfH, halfH, -1.0f, 100.0f);
  }

  /**
   * Configures the camera for 3D rendering with a perspective projection.
   *
   * @param fov          The vertical field of view, in degrees.
   * @param aspectRatio  The aspect ratio of the viewport (width / height).
   * @param nearPlane    The distance to the near clipping plane.
   * @param farPlane     The distance to the far clipping plane.
   */
  public final void setPerspective(float fov, float aspectRatio, float nearPlane, float farPlane) {
    this.projectionType = ProjectionType.PERSPECTIVE;
    this.fov = fov;
    this.nearPlane = nearPlane;
    this.farPlane = farPlane;
    projectionMatrix.identity().perspective((float) Math.toRadians(fov), aspectRatio, nearPlane, farPlane);
  }

  /**
   * Updates the camera's projection matrix to match a new screen size,
   * maintaining the original aspect ratio to prevent distortion.
   *
   * @param screenWidth  The new width of the window's framebuffer.
   * @param screenHeight The new height of the window's framebuffer.
   */
  public void resize(int screenWidth, int screenHeight) {
    float newAspectRatio = (float) screenWidth / (float) screenHeight;

    if (projectionType == ProjectionType.PERSPECTIVE) {
      setPerspective(this.fov, newAspectRatio, this.nearPlane, this.farPlane);
    } else if (projectionType == ProjectionType.ORTHOGRAPHIC) {
      float targetAspectRatio = orthoWidth / orthoHeight;
      float left, right, bottom, top;

      // This logic ensures the original world view is always visible, expanding
      // in one dimension to fill the new window shape (pillarboxing/letterboxing).
      if (newAspectRatio >= targetAspectRatio) {
        // Window is wider than target, so height is the constraining dimension
        float scaledWidth = orthoHeight * newAspectRatio;
        left = -scaledWidth / 2.0f;
        right = scaledWidth / 2.0f;
        bottom = -orthoHeight / 2.0f;
        top = orthoHeight / 2.0f;
      } else {
        // Window is taller than target, so width is the constraining dimension
        float scaledHeight = orthoWidth / newAspectRatio;
        left = -orthoWidth / 2.0f;
        right = orthoWidth / 2.0f;
        bottom = -scaledHeight / 2.0f;
        top = scaledHeight / 2.0f;
      }
      projectionMatrix.identity().ortho(left, right, bottom, top, -1.0f, 100.0f);
    }
  }
}
