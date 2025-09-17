package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MeshDefinition(
  String handle,
  float[] vertices,
  int[] indices
) {
  @JsonCreator
  public MeshDefinition(
    @JsonProperty("handle") String handle,
    @JsonProperty("vertices") float[] vertices,
    @JsonProperty("indices") int[] indices
  ) {
    this.handle = handle;
    this.vertices = vertices;
    this.indices = indices;
  }
}
