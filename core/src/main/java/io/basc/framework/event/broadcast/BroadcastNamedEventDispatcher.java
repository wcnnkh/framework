package io.basc.framework.event.broadcast;

import io.basc.framework.event.NamedEventDispatcher;

public interface BroadcastNamedEventDispatcher<K, T>
		extends NamedEventDispatcher<K, T>, BroadcastNamedEventRegistry<K, T> {
}