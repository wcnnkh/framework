package io.basc.framework.data.template;

import io.basc.framework.codec.Encoder;
import io.basc.framework.data.kv.KeyOperationsWrapper;

public interface TemporaryKeyTemplateWrapper<K> extends KeyOperationsWrapper<K>, TemporaryKeyTemplate<K> {

	@Override
	TemporaryKeyTemplate<K> getSourceOperations();

	@Override
	default boolean touch(K key) {
		Encoder<K, K> fomatter = getKeyFomatter();
		return getSourceOperations().touch(fomatter == null ? key : fomatter.encode(key));
	}
}
