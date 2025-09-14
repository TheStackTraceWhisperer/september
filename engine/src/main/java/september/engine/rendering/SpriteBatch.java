package september.engine.rendering;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Groups sprites by their texture for efficient instanced rendering.
 * <p>
 * This class batches sprites that share the same texture so they can be rendered
 * together using instanced rendering, reducing the number of draw calls from
 * one-per-sprite to one-per-texture.
 * <p>
 * Usage pattern:
 * 1. Clear the batch at the start of the frame
 * 2. Add sprites throughout the frame  
 * 3. Render all batches at the end of the frame
 */
public class SpriteBatch {

  private final Map<Texture, List<Matrix4f>> batches;
  private final int maxInstancesPerBatch;

  /**
   * Creates a new sprite batch with the specified maximum instances per batch.
   *
   * @param maxInstancesPerBatch Maximum number of sprites per texture batch.
   */
  public SpriteBatch(int maxInstancesPerBatch) {
    this.maxInstancesPerBatch = maxInstancesPerBatch;
    this.batches = new HashMap<>();
  }

  /**
   * Creates a new sprite batch with a default maximum instances per batch.
   */
  public SpriteBatch() {
    this(1000); // Default to 1000 instances per batch
  }

  /**
   * Adds a sprite to the batch for the given texture.
   *
   * @param texture   The texture used by this sprite.
   * @param transform The transformation matrix for this sprite.
   */
  public void addSprite(Texture texture, Matrix4f transform) {
    batches.computeIfAbsent(texture, k -> new ArrayList<>()).add(transform);
  }

  /**
   * Gets all textures that have sprites in this batch.
   *
   * @return An iterable of textures.
   */
  public Iterable<Texture> getTextures() {
    return batches.keySet();
  }

  /**
   * Gets the transformation matrices for all sprites using the given texture.
   *
   * @param texture The texture to query.
   * @return A list of transformation matrices, or an empty list if no sprites use this texture.
   */
  public List<Matrix4f> getSpritesForTexture(Texture texture) {
    return batches.getOrDefault(texture, new ArrayList<>());
  }

  /**
   * Gets the number of sprites using the given texture.
   *
   * @param texture The texture to query.
   * @return The number of sprites for this texture.
   */
  public int getSpriteCount(Texture texture) {
    return batches.getOrDefault(texture, new ArrayList<>()).size();
  }

  /**
   * Gets the total number of sprites in all batches.
   *
   * @return The total sprite count.
   */
  public int getTotalSpriteCount() {
    return batches.values().stream().mapToInt(List::size).sum();
  }

  /**
   * Gets the number of distinct textures (and thus draw calls) in this batch.
   *
   * @return The number of batches.
   */
  public int getBatchCount() {
    return batches.size();
  }

  /**
   * Checks if any sprites exceed the maximum instances per batch.
   * This can help detect when batching might not be optimal.
   *
   * @return True if any texture has more sprites than the maximum.
   */
  public boolean hasOversizedBatches() {
    return batches.values().stream().anyMatch(sprites -> sprites.size() > maxInstancesPerBatch);
  }

  /**
   * Clears all batches, preparing for a new frame.
   */
  public void clear() {
    batches.clear();
  }

  /**
   * Checks if the batch is empty.
   *
   * @return True if no sprites have been added.
   */
  public boolean isEmpty() {
    return batches.isEmpty();
  }

  /**
   * Gets the maximum number of instances per batch.
   *
   * @return The maximum instance count.
   */
  public int getMaxInstancesPerBatch() {
    return maxInstancesPerBatch;
  }
}