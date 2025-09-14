package september.engine.core.preferences;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertyTypeTest {
    
    @Test
    void stringType_shouldSerializeAndDeserialize() {
        // Given
        String value = "test string";
        
        // When
        String serialized = PropertyType.STRING.serialize(value);
        String deserialized = PropertyType.STRING.deserialize(serialized);
        
        // Then
        assertThat(serialized).isEqualTo(value);
        assertThat(deserialized).isEqualTo(value);
    }
    
    @Test
    void integerType_shouldSerializeAndDeserialize() {
        // Given
        Integer value = 42;
        
        // When
        String serialized = PropertyType.INTEGER.serialize(value);
        Integer deserialized = PropertyType.INTEGER.deserialize(serialized);
        
        // Then
        assertThat(serialized).isEqualTo("42");
        assertThat(deserialized).isEqualTo(value);
    }
    
    @Test
    void booleanType_shouldSerializeAndDeserialize() {
        // Given
        Boolean value = true;
        
        // When
        String serialized = PropertyType.BOOLEAN.serialize(value);
        Boolean deserialized = PropertyType.BOOLEAN.deserialize(serialized);
        
        // Then
        assertThat(serialized).isEqualTo("true");
        assertThat(deserialized).isEqualTo(value);
    }
    
    @Test
    void jsonType_shouldSerializeAndDeserializeComplexObjects() {
        // Given
        record TestData(String name, int value, boolean flag) {}
        PropertyType<TestData> jsonType = PropertyType.json(TestData.class);
        TestData original = new TestData("test", 42, true);
        
        // When
        String serialized = jsonType.serialize(original);
        TestData deserialized = jsonType.deserialize(serialized);
        
        // Then
        assertThat(serialized).contains("test");
        assertThat(serialized).contains("42");
        assertThat(serialized).contains("true");
        assertThat(deserialized).isEqualTo(original);
        assertThat(deserialized.name()).isEqualTo("test");
        assertThat(deserialized.value()).isEqualTo(42);
        assertThat(deserialized.flag()).isTrue();
    }
    
    @Test
    void jsonType_shouldThrowOnInvalidJson() {
        // Given
        PropertyType<String> jsonType = PropertyType.json(String.class);
        String invalidJson = "{ invalid json";
        
        // When & Then
        assertThatThrownBy(() -> jsonType.deserialize(invalidJson))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to deserialize JSON");
    }
    
    @Test
    void doubleType_shouldHandleDecimalValues() {
        // Given
        Double value = 3.14159;
        
        // When
        String serialized = PropertyType.DOUBLE.serialize(value);
        Double deserialized = PropertyType.DOUBLE.deserialize(serialized);
        
        // Then
        assertThat(serialized).isEqualTo("3.14159");
        assertThat(deserialized).isEqualTo(value);
    }
    
    @Test
    void longType_shouldHandleLargeNumbers() {
        // Given
        Long value = Long.MAX_VALUE;
        
        // When
        String serialized = PropertyType.LONG.serialize(value);
        Long deserialized = PropertyType.LONG.deserialize(serialized);
        
        // Then
        assertThat(deserialized).isEqualTo(value);
    }
}