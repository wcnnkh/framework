package run.soeasy.framework.core.streaming;

import run.soeasy.framework.core.domain.KeyValue;

public class EmptyMapping<K, V> extends EmptyStreamable<KeyValue<K, V>> implements Mapping<K, V> {
	private static final long serialVersionUID = 1L;

	static final EmptyMapping<?, ?> INSTANCE = new EmptyMapping<>();
}
