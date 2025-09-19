package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AssetManifest(
  List<TextureDefinition> textures,
  List<MeshDefinition> meshes
) {
  @JsonCreator
  public AssetManifest(
    @JsonProperty("textures") List<TextureDefinition> textures,
    @JsonProperty("meshes") List<MeshDefinition> meshes
  ) {
    this.textures = textures != null ? textures : List.of();
    this.meshes = meshes != null ? meshes : List.of();
  }
}

