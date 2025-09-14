package september.game.components;

import september.engine.ecs.components.SoundEffectComponent;

/**
 * Defines the specific types of sound effects for this particular game.
 * This enum implements the engine's generic SoundEffectType interface.
 * <p>
 * Each sound type can be used to apply different volume levels, processing,
 * or other audio settings in the AudioSystem.
 */
public enum GameSoundEffectType implements SoundEffectComponent.SoundEffectType {
  // UI sounds
  UI_BUTTON_CLICK,
  UI_BUTTON_HOVER,
  UI_MENU_OPEN,
  UI_MENU_CLOSE,
  UI_ERROR,
  
  // Player actions
  PLAYER_JUMP,
  PLAYER_LAND,
  PLAYER_FOOTSTEP,
  PLAYER_DAMAGE,
  PLAYER_HEAL,
  
  // Combat sounds
  WEAPON_SWING,
  WEAPON_HIT,
  PROJECTILE_FIRE,
  PROJECTILE_HIT,
  
  // Environment sounds
  ITEM_PICKUP,
  DOOR_OPEN,
  DOOR_CLOSE,
  SWITCH_ACTIVATE,
  
  // Enemy sounds
  ENEMY_DAMAGE,
  ENEMY_DEATH,
  ENEMY_ATTACK
}