package september.engine.core.preferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import static org.assertj.core.api.Assertions.assertThat;

class PreferencesServiceTest {

    private PreferencesService preferencesService;

    @BeforeEach
    void setUp() {
        // Use a unique node name for each test to avoid interference
        String testNodeName = "test-" + System.currentTimeMillis();
        preferencesService = new PreferencesService(testNodeName); // Short debounce for testing
    }

    @AfterEach
    void tearDown() throws Exception {
        if (preferencesService != null) {
            preferencesService.close();
        }
    }

    @Test
    void createProperty_shouldReturnPropertyWithDefaultValue() {
        // Given
        String key = "test.string.property";
        String defaultValue = "default";

        // When
        Property<String> property = preferencesService.createProperty(key, defaultValue, PropertyType.STRING);

        // Then
        assertThat(property.get()).isEqualTo(defaultValue);
        assertThat(property.getDefault()).isEqualTo(defaultValue);
        assertThat(property.getKey()).isEqualTo(key);
        assertThat(property.isModified()).isFalse();
        assertThat(property.isDirty()).isFalse();
    }

    @Test
    void setProperty_shouldMarkAsDirtyAndModified() {
        // Given
        Property<String> property = preferencesService.createProperty("test.key", "default", PropertyType.STRING);
        String newValue = "new value";

        // When
        property.set(newValue);

        // Then
        assertThat(property.get()).isEqualTo(newValue);
        assertThat(property.isDirty()).isTrue();
        assertThat(property.isModified()).isTrue();
    }

    @Test
    void flush_shouldPersistChanges() throws Exception {
        // Given
        Property<Integer> property = preferencesService.createProperty("test.int", 42, PropertyType.INTEGER);
        Integer newValue = 100;
        property.set(newValue);

        // When
        preferencesService.flush();

        // Then
        assertThat(property.isDirty()).isFalse();

        // Create a new service with the same node to verify persistence
        String testNodeName = ((PreferencesService) preferencesService).getNodeName();
        try (PreferencesService newService = new PreferencesService(testNodeName)) {
            Property<Integer> reloadedProperty = newService.createProperty("test.int", 42, PropertyType.INTEGER);
            assertThat(reloadedProperty.get()).isEqualTo(newValue);
        }
    }

    @Test
    void revert_shouldRestoreOriginalValue() {
        // Given
        Property<String> property = preferencesService.createProperty("test.revert", "original", PropertyType.STRING);
        property.set("modified");

        // When
        property.revert();

        // Then
        assertThat(property.get()).isEqualTo("original");
        assertThat(property.isDirty()).isFalse();
        assertThat(property.isModified()).isFalse();
    }

    @Test
    void explicitSaveModel_shouldNotAutomaticallySave() throws Exception {
        // Given
        Property<String> property = preferencesService.createProperty("test.explicit", "original", PropertyType.STRING);
        property.set("modified");

        // When - don't flush immediately, with explicit save model
        assertThat(property.isDirty()).isTrue();

        // Wait some time
        Thread.sleep(100);

        // Then - should still be dirty since no explicit flush was called
        assertThat(property.isDirty()).isTrue();

        // When we explicitly flush
        preferencesService.flush();

        // Then - should no longer be dirty
        assertThat(property.isDirty()).isFalse();
    }

    @Test
    void jsonPropertyType_shouldSerializeComplexObjects() {
        // Given
        record TestData(String name, int value) {}
        PropertyType<TestData> jsonType = PropertyType.json(TestData.class);
        TestData defaultData = new TestData("default", 0);

        // When
        Property<TestData> property = preferencesService.createProperty("test.json", defaultData, jsonType);
        TestData newData = new TestData("test", 42);
        property.set(newData);

        // Then
        assertThat(property.get()).isEqualTo(newData);
        assertThat(property.get().name()).isEqualTo("test");
        assertThat(property.get().value()).isEqualTo(42);
    }

    @Test
    void multipleProperties_shouldWorkIndependently() {
        // Given
        Property<String> stringProp = preferencesService.createProperty("test.string", "default", PropertyType.STRING);
        Property<Integer> intProp = preferencesService.createProperty("test.int", 0, PropertyType.INTEGER);
        Property<Boolean> boolProp = preferencesService.createProperty("test.bool", false, PropertyType.BOOLEAN);

        // When
        stringProp.set("test");
        intProp.set(42);
        boolProp.set(true);

        // Then
        assertThat(stringProp.get()).isEqualTo("test");
        assertThat(intProp.get()).isEqualTo(42);
        assertThat(boolProp.get()).isTrue();

        assertThat(stringProp.isDirty()).isTrue();
        assertThat(intProp.isDirty()).isTrue();
        assertThat(boolProp.isDirty()).isTrue();
    }
}
