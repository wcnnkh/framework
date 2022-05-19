package io.basc.framework.data.domain;

public interface FactoryOption<K, V> {
	Option<K, V> getOption();
}