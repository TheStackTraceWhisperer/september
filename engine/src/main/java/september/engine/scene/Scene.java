package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Scene(
  @JsonProperty("name") String name,
  @JsonProperty("manifest") AssetManifest manifest,
  @JsonProperty("entities") List<EntityTemplate> entities
) {

}
