package run.soeasy.framework.core.domain;

import java.util.Map.Entry;

@FunctionalInterface
public interface EntryWrapper<K, V, W extends Entry<K, V>> extends Entry<K, V>, KeyValue<K, V>, Wrapper<W> {
	@Override
	default K getKey() {
		return getSource().getKey();
	}

	@Override
	default V getValue() {
		return getSource().getValue();
	}

	@Override
	default V setValue(V value) {
		return getSource().setValue(value);
	}
}
