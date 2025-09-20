package september.engine.scene.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.joml.Vector3f;

import java.io.IOException;

public class Vector3fDeserializer extends StdDeserializer<Vector3f> {

  public Vector3fDeserializer() {
    this(null);
  }

  public Vector3fDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Vector3f deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    float x = (float) node.get("x").asDouble();
    float y = (float) node.get("y").asDouble();
    float z = (float) node.get("z").asDouble();
    return new Vector3f(x, y, z);
  }
}
