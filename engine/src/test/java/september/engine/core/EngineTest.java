package september.engine.core;

import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to validate the basic initialization of the Engine via the EngineTestHarness.
 */
class EngineTest extends EngineTestHarness {

    @Test
    void harness_correctly_initializes_engine_and_services() {
        // The @BeforeEach in EngineTestHarness should have already run.
        // This test simply verifies that the core components are not null.

        assertThat(engine).as("Engine should be initialized by the harness.").isNotNull();
        assertThat(world).as("World should be initialized by the harness.").isNotNull();
        assertThat(resourceManager).as("ResourceManager should be initialized by the harness.").isNotNull();
        assertThat(camera).as("Camera should be initialized by the harness.").isNotNull();
    }

    @Test
    void engine_init_creates_gl_context_and_renderer() {
        // This test verifies that the engine's init() method, called by the harness,
        // successfully created the necessary OpenGL-dependent objects.

        assertThat(engine.getWindow()).as("WindowContext should be created during engine initialization.").isNotNull();
        assertThat(engine.getRenderer()).as("Renderer should be created during engine initialization.").isNotNull();
    }
}
