package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Scene(
  String name,
  List<EntityTemplate> entities
) {
  @JsonCreator
  public Scene(
    @JsonProperty("name") String name,
    @JsonProperty("entities") List<EntityTemplate> entities
  ) {
    this.name = name;
    this.entities = entities;
  }
}
