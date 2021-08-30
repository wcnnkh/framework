package io.basc.framework.event;

@FunctionalInterface
public interface EventListener<T extends Event> extends java.util.EventListener {
	void onEvent(T event);
}
