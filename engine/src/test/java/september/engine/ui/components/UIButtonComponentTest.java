package september.engine.ui.components;

import org.joml.Vector4f;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UIButtonComponentTest {
  @Test
  @DisplayName("Constructor should set all fields and default colors correctly")
  void constructor_setsAllFieldsAndDefaults() {
    var component = new UIButtonComponent("START_GAME", "norm", "hover", "press", null, null, null);

    assertThat(component.actionEvent).isEqualTo("START_GAME");
    assertThat(component.normalTexture).isEqualTo("norm");
    assertThat(component.hoveredTexture).isEqualTo("hover");
    assertThat(component.pressedTexture).isEqualTo("press");
    assertThat(component.normalColor).isEqualTo(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
    assertThat(component.hoveredColor).isEqualTo(new Vector4f(0.9f, 0.9f, 0.9f, 1.0f));
    assertThat(component.pressedColor).isEqualTo(new Vector4f(0.7f, 0.7f, 0.7f, 1.0f));
    assertThat(component.currentState).isEqualTo(UIButtonComponent.ButtonState.NORMAL);
  }
}
