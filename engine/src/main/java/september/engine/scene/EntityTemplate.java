package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record EntityTemplate(
  @JsonProperty("name") String name,
  @JsonProperty("components") Map<String, Object> components
) {

}
