package september.engine.events;

/**
 * Published by the UISystem when a UI button is successfully clicked.
 *
 * @param actionEvent The custom action string from the UIButtonComponent.
 */
public record UIButtonClickedEvent(String actionEvent) implements Event {
}
