package september.engine.events;

import september.engine.state.GameState;

public record StateChangeEvent(
  GameState gameState
) implements Event {
}
