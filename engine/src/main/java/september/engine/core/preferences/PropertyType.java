package september.engine.core.preferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Function;

/**
 * Defines how properties are serialized to and deserialized from preferences storage.
 * Provides built-in types for common Java types and JSON serialization for complex objects.
 *
 * @param <T> the type this PropertyType handles
 */
public final class PropertyType<T> {

  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  private final Function<String, T> deserializer;
  private final Function<T, String> serializer;

  private PropertyType(Function<T, String> serializer, Function<String, T> deserializer) {
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  /**
   * Serializes a value to a string for storage.
   */
  public String serialize(T value) {
    return serializer.apply(value);
  }

  /**
   * Deserializes a string from storage to the target type.
   */
  public T deserialize(String value) {
    return deserializer.apply(value);
  }

  // Built-in types for common use cases
  public static final PropertyType<String> STRING = new PropertyType<>(
    s -> s,
    s -> s
  );

  public static final PropertyType<Integer> INTEGER = new PropertyType<>(
    Object::toString,
    Integer::valueOf
  );

  public static final PropertyType<Long> LONG = new PropertyType<>(
    Object::toString,
    Long::valueOf
  );

  public static final PropertyType<Double> DOUBLE = new PropertyType<>(
    Object::toString,
    Double::valueOf
  );

  public static final PropertyType<Float> FLOAT = new PropertyType<>(
    Object::toString,
    Float::valueOf
  );

  public static final PropertyType<Boolean> BOOLEAN = new PropertyType<>(
    Object::toString,
    Boolean::valueOf
  );

  /**
   * Creates a PropertyType for arbitrary objects using JSON serialization.
   *
   * @param <T>   the type to serialize
   * @param clazz the Class object for the type
   * @return a PropertyType that uses JSON for serialization
   */
  public static <T> PropertyType<T> json(Class<T> clazz) {
    return new PropertyType<>(
      value -> {
        try {
          return JSON_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Failed to serialize to JSON: " + value, e);
        }
      },
      json -> {
        try {
          return JSON_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Failed to deserialize JSON: " + json, e);
        }
      }
    );
  }
}
