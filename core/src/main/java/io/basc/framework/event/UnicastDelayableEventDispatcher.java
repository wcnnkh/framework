package io.basc.framework.event;

public interface UnicastDelayableEventDispatcher<T> extends EventDispatcher<T>, UnicastEventDispatcher<T> {
}
