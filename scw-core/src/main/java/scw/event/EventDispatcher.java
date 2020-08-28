package scw.event;

public interface EventDispatcher<T extends Event>
		extends BasicEventDispatcher<T>, TypeEventDispathcer<T>, NamedEventDispatcher<T> {
}