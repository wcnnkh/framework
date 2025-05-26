package run.soeasy.framework.core.domain;

public interface KeyValueWrapper<K, V, W extends KeyValue<K, V>> extends KeyValue<K, V>, Wrapper<W> {
	@Override
	default K getKey() {
		return getSource().getKey();
	}

	@Override
	default V getValue() {
		return getSource().getValue();
	}
}
