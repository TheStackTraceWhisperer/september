package september.engine.ecs.components;

import org.junit.jupiter.api.Test;
import september.game.components.GameSoundEffectType;

import static org.assertj.core.api.Assertions.assertThat;

class SoundEffectComponentTest {

  @Test
  void constructor_withRequiredParameters_setsDefaultValues() {
    // Arrange & Act
    SoundEffectComponent component = new SoundEffectComponent("click.ogg", GameSoundEffectType.UI_BUTTON_CLICK);

    // Assert
    assertThat(component.soundBufferHandle).isEqualTo("click.ogg");
    assertThat(component.soundType).isEqualTo(GameSoundEffectType.UI_BUTTON_CLICK);
    assertThat(component.volume).isEqualTo(1.0f);
    assertThat(component.pitch).isEqualTo(1.0f);
    assertThat(component.autoPlay).isTrue();
    assertThat(component.removeAfterPlay).isTrue();
    assertThat(component.hasBeenTriggered).isFalse();
  }

  @Test
  void constructor_withVolumeParameter_setsCorrectVolume() {
    // Arrange & Act
    SoundEffectComponent component = new SoundEffectComponent("footstep.ogg", GameSoundEffectType.PLAYER_FOOTSTEP, 0.6f);

    // Assert
    assertThat(component.soundBufferHandle).isEqualTo("footstep.ogg");
    assertThat(component.soundType).isEqualTo(GameSoundEffectType.PLAYER_FOOTSTEP);
    assertThat(component.volume).isEqualTo(0.6f);
    assertThat(component.pitch).isEqualTo(1.0f); // Default value
  }

  @Test
  void constructor_withAllParameters_setsAllValues() {
    // Arrange & Act
    SoundEffectComponent component = new SoundEffectComponent("hit.ogg", GameSoundEffectType.WEAPON_HIT, 0.8f, 1.2f);

    // Assert
    assertThat(component.soundBufferHandle).isEqualTo("hit.ogg");
    assertThat(component.soundType).isEqualTo(GameSoundEffectType.WEAPON_HIT);
    assertThat(component.volume).isEqualTo(0.8f);
    assertThat(component.pitch).isEqualTo(1.2f);
    assertThat(component.autoPlay).isTrue(); // Default value
    assertThat(component.removeAfterPlay).isTrue(); // Default value
  }

  @Test
  void soundEffectType_implementsCorrectInterface() {
    // Arrange & Act
    SoundEffectComponent component = new SoundEffectComponent("test.ogg", GameSoundEffectType.UI_ERROR);

    // Assert - Testing the interface implementation
    assertThat(component.soundType).isInstanceOf(SoundEffectComponent.SoundEffectType.class);
    assertThat(GameSoundEffectType.UI_ERROR).isInstanceOf(SoundEffectComponent.SoundEffectType.class);
  }
}