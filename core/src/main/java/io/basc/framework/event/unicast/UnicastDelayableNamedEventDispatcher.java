package io.basc.framework.event.unicast;

import io.basc.framework.event.DelayableNamedEventDispatcher;

public interface UnicastDelayableNamedEventDispatcher<K, T>
		extends UnicastNamedEventDispatcher<K, T>, DelayableNamedEventDispatcher<K, T> {
}
