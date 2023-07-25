package io.basc.framework.event.unicast;

import io.basc.framework.event.NamedEventDispatcher;

public interface UnicastNamedEventDispatcher<K, T> extends NamedEventDispatcher<K, T>, UnicastNamedEventRegistry<K, T> {
}