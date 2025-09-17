package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TextureDefinition(
  String handle,
  String path
) {
  @JsonCreator
  public TextureDefinition(
    @JsonProperty("handle") String handle,
    @JsonProperty("path") String path
  ) {
    this.handle = handle;
    this.path = path;
  }

}
