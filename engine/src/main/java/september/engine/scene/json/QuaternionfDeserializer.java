package september.engine.scene.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.joml.Quaternionf;

import java.io.IOException;

public class QuaternionfDeserializer extends StdDeserializer<Quaternionf> {

    public QuaternionfDeserializer() {
        this(null);
    }

    public QuaternionfDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Quaternionf deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        float x = (float) node.get("x").asDouble();
        float y = (float) node.get("y").asDouble();
        float z = (float) node.get("z").asDouble();
        float w = (float) node.get("w").asDouble();
        return new Quaternionf(x, y, z, w);
    }
}
