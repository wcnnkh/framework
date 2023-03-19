package io.basc.framework.event;

public interface UnicastNamedEventDispatcher<K, T> extends NamedEventDispatcher<K, T>, UnicastNamedEventRegistry<K, T> {
}