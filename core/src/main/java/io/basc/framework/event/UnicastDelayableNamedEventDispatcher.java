package io.basc.framework.event;

public interface UnicastDelayableNamedEventDispatcher<K, T>
		extends UnicastNamedEventDispatcher<K, T>, DelayableNamedEventDispatcher<K, T> {
}
