package september.engine.ecs.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MusicComponentTest {

  private MusicComponent musicComponent;

  @BeforeEach
  void setUp() {
    musicComponent = new MusicComponent("background-music.ogg");
  }

  @Test
  void constructor_withBufferHandle_setsDefaultValues() {
    // Assert
    assertThat(musicComponent.musicBufferHandle).isEqualTo("background-music.ogg");
    assertThat(musicComponent.baseVolume).isEqualTo(1.0f);
    assertThat(musicComponent.currentVolume).isEqualTo(1.0f);
    assertThat(musicComponent.looping).isTrue();
    assertThat(musicComponent.autoPlay).isTrue();
    assertThat(musicComponent.fadingIn).isFalse();
    assertThat(musicComponent.fadingOut).isFalse();
    assertThat(musicComponent.fadeDuration).isEqualTo(2.0f);
    assertThat(musicComponent.fadeTimer).isEqualTo(0.0f);
    assertThat(musicComponent.isPlaying).isFalse();
    assertThat(musicComponent.isPaused).isFalse();
  }

  @Test
  void constructor_withAllParameters_setsCorrectValues() {
    // Arrange & Act
    MusicComponent component = new MusicComponent("epic-music.ogg", 0.7f, false, 3.5f);

    // Assert
    assertThat(component.musicBufferHandle).isEqualTo("epic-music.ogg");
    assertThat(component.baseVolume).isEqualTo(0.7f);
    assertThat(component.currentVolume).isEqualTo(0.7f);
    assertThat(component.looping).isFalse();
    assertThat(component.fadeDuration).isEqualTo(3.5f);
  }

  @Test
  void startFadeIn_initializesFadeInState() {
    // Act
    musicComponent.startFadeIn();

    // Assert
    assertThat(musicComponent.fadingIn).isTrue();
    assertThat(musicComponent.fadingOut).isFalse();
    assertThat(musicComponent.fadeTimer).isEqualTo(0.0f);
    assertThat(musicComponent.currentVolume).isEqualTo(0.0f);
  }

  @Test
  void startFadeOut_initializesFadeOutState() {
    // Arrange
    musicComponent.currentVolume = 0.8f;

    // Act
    musicComponent.startFadeOut();

    // Assert
    assertThat(musicComponent.fadingOut).isTrue();
    assertThat(musicComponent.fadingIn).isFalse();
    assertThat(musicComponent.fadeTimer).isEqualTo(0.0f);
    // currentVolume should remain unchanged initially
    assertThat(musicComponent.currentVolume).isEqualTo(0.8f);
  }

  @Test
  void stopFade_resetsAllFadeState() {
    // Arrange
    musicComponent.startFadeIn();
    musicComponent.fadeTimer = 1.0f;

    // Act
    musicComponent.stopFade();

    // Assert
    assertThat(musicComponent.fadingIn).isFalse();
    assertThat(musicComponent.fadingOut).isFalse();
    assertThat(musicComponent.fadeTimer).isEqualTo(0.0f);
    assertThat(musicComponent.currentVolume).isEqualTo(musicComponent.baseVolume);
  }

  @Test
  void startFadeIn_overridesPreviousFadeOut() {
    // Arrange
    musicComponent.startFadeOut();
    assertThat(musicComponent.fadingOut).isTrue();

    // Act
    musicComponent.startFadeIn();

    // Assert
    assertThat(musicComponent.fadingIn).isTrue();
    assertThat(musicComponent.fadingOut).isFalse();
    assertThat(musicComponent.fadeTimer).isEqualTo(0.0f);
  }

  @Test
  void startFadeOut_overridesPreviousFadeIn() {
    // Arrange
    musicComponent.startFadeIn();
    assertThat(musicComponent.fadingIn).isTrue();

    // Act
    musicComponent.startFadeOut();

    // Assert
    assertThat(musicComponent.fadingOut).isTrue();
    assertThat(musicComponent.fadingIn).isFalse();
    assertThat(musicComponent.fadeTimer).isEqualTo(0.0f);
  }
}