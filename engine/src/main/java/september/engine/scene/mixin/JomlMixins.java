package september.engine.scene.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class JomlMixins {
    /**
     * This mix-in tells Jackson to ignore certain properties of the Matrix4f class
     * during deserialization. This is necessary because Matrix4f has multiple
     * overloaded setters (e.g., setTransposed) that confuse Jackson, leading to
     * an InvalidDefinitionException. By ignoring these, we let Jackson use the
     * default constructor and then set the fields it understands, avoiding the error.
     */
    @JsonIgnoreProperties({"transposed", "properties", "scale", "translation", "rotation"})
    public abstract static class Matrix4fMixin {}

    /**
     * A mix-in for Vector3f. While not strictly necessary to fix the current error,
     * it's good practice to have it ready for consistent serialization behavior.
     */
    public abstract static class Vector3fMixin {}
}
