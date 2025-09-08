package september.engine.rendering;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents the viewer in the 3D scene.
 * Manages the view and projection matrices required for rendering.
 */
public class Camera {
  @Getter
  private final Matrix4f projectionMatrix;
  private final Matrix4f viewMatrix;

  @Getter
  private final Vector3f position;
  private final Vector3f direction;

  public Camera() {
    // A basic perspective projection
    this.projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(45.0f), 16.0f / 9.0f, 0.1f, 100.0f);
    this.viewMatrix = new Matrix4f();
    this.position = new Vector3f(0, 0, 5);
    this.direction = new Vector3f(0, 0, -1);
  }

  public Matrix4f getViewMatrix() {
    // The view matrix moves the world opposite to the camera's movement
    Vector3f target = new Vector3f(position).add(direction); // Look in the direction
    Vector3f up = new Vector3f(0, 1, 0);
    return viewMatrix.identity().lookAt(position, target, up);
  }

}
