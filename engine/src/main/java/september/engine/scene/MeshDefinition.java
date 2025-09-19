package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MeshDefinition(
  @JsonProperty("handle") String handle,
  @JsonProperty("vertices") float[] vertices,
  @JsonProperty("indices") int[] indices
) {

}
