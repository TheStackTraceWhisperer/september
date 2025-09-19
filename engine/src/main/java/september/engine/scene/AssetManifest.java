package september.engine.scene;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AssetManifest(
  @JsonProperty("textures") List<TextureDefinition> textures,
  @JsonProperty("meshes") List<MeshDefinition> meshes
) {

}

