package io.basc.framework.event.broadcast;

import io.basc.framework.event.DelayableNamedEventDispatcher;

public interface BroadcastDelayableNamedEventDispatcher<K, T>
		extends BroadcastNamedEventDispatcher<K, T>, DelayableNamedEventDispatcher<K, T> {
}