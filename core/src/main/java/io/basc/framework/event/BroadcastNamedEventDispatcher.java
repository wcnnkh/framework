package io.basc.framework.event;

public interface BroadcastNamedEventDispatcher<K, T>
		extends NamedEventDispatcher<K, T>, BroadcastNamedEventRegistry<K, T> {
}