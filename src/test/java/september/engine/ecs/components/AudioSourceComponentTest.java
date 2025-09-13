package september.engine.ecs.components;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AudioSourceComponentTest {

  @Test
  void constructor_withBufferHandle_setsDefaultValues() {
    // Arrange & Act
    AudioSourceComponent component = new AudioSourceComponent("test-audio.ogg");

    // Assert
    assertThat(component.audioBufferHandle).isEqualTo("test-audio.ogg");
    assertThat(component.volume).isEqualTo(1.0f);
    assertThat(component.pitch).isEqualTo(1.0f);
    assertThat(component.looping).isFalse();
    assertThat(component.autoPlay).isFalse();
    assertThat(component.isPlaying).isFalse();
  }

  @Test
  void constructor_withAllParameters_setsAllValues() {
    // Arrange & Act
    AudioSourceComponent component = new AudioSourceComponent("test-audio.ogg", 0.5f, true, true);

    // Assert
    assertThat(component.audioBufferHandle).isEqualTo("test-audio.ogg");
    assertThat(component.volume).isEqualTo(0.5f);
    assertThat(component.looping).isTrue();
    assertThat(component.autoPlay).isTrue();
    assertThat(component.pitch).isEqualTo(1.0f); // Default value
    assertThat(component.isPlaying).isFalse(); // Default transient value
  }

  @Test
  void constructor_withVolumeAndLooping_setsCorrectValues() {
    // Arrange & Act
    AudioSourceComponent component = new AudioSourceComponent("background.ogg", 0.8f, true);

    // Assert
    assertThat(component.audioBufferHandle).isEqualTo("background.ogg");
    assertThat(component.volume).isEqualTo(0.8f);
    assertThat(component.looping).isTrue();
    assertThat(component.autoPlay).isFalse(); // Default value
  }
}