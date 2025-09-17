package september.engine.scene.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CustomJomlModule extends SimpleModule {

    public CustomJomlModule() {
        super("CustomJomlModule", new Version(1, 0, 0, null, null, null));
        addDeserializer(Vector3f.class, new Vector3fDeserializer());
        addDeserializer(Quaternionf.class, new QuaternionfDeserializer());
        // We do not need a Matrix4f deserializer as it's a calculated property.
    }
}
