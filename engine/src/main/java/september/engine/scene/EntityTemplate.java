package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record EntityTemplate(String name, Map<String, Object> components) {
  @JsonCreator
  public EntityTemplate(
    @JsonProperty("name") String name,
    @JsonProperty("components") Map<String, Object> components) {
    this.name = name;
    this.components = components;
  }
}
