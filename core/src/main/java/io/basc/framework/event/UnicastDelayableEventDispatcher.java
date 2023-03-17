package io.basc.framework.event;

public interface UnicastDelayableEventDispatcher<T> extends DelayableEventDispatcher<T>, UnicastEventDispatcher<T> {
}
