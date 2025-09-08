package september.engine.ecs.components;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import september.engine.ecs.Component;

/**
 * Represents the position, rotation, and scale of an entity in 3D space.
 * This is a fundamental component for any entity that exists in the game world.
 */
public class TransformComponent implements Component {

  public final Vector3f position;
  public final Vector3f previousPosition; // Stores the position from the previous frame for collision response
  public final Quaternionf rotation; // Using quaternions is more robust than Euler angles for rotation
  public final Vector3f scale;

  private final Matrix4f transformMatrix;

  public TransformComponent() {
    this.position = new Vector3f(0.0f, 0.0f, 0.0f);
    this.previousPosition = new Vector3f(0.0f, 0.0f, 0.0f);
    this.rotation = new Quaternionf().identity();
    this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
    this.transformMatrix = new Matrix4f().identity();
  }

  /**
   * Stores the current position as the previous position.
   * This should be called once per frame before any movement logic.
   */
  public void updatePreviousPosition() {
    this.previousPosition.set(this.position);
  }

  /**
   * Reverts the current position to the stored previous position.
   * This is the core of our collision response.
   */
  public void revertPosition() {
    this.position.set(this.previousPosition);
  }

  /**
   * Calculates and returns the model-to-world transformation matrix for this transform.
   * This matrix is what will be passed to the shader to render the object in the correct location.
   *
   * @return A Matrix4f representing the combined transformation.
   */
  public Matrix4f getTransformMatrix() {
    return transformMatrix.identity()
      .translate(position)
      .rotate(rotation)
      .scale(scale);
  }
}
