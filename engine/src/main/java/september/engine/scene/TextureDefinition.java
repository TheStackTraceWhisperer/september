package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TextureDefinition(
  @JsonProperty("handle") String handle,
  @JsonProperty("path") String path
) {

}
