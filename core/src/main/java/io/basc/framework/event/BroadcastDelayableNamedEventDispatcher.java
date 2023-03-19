package io.basc.framework.event;

public interface BroadcastDelayableNamedEventDispatcher<K, T>
		extends BroadcastNamedEventDispatcher<K, T>, DelayableNamedEventDispatcher<K, T> {
}