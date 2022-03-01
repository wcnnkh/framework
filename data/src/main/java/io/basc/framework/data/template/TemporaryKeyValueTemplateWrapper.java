package io.basc.framework.data.template;

import io.basc.framework.codec.Encoder;
import io.basc.framework.data.kv.KeyValueOperationsWrapper;

public interface TemporaryKeyValueTemplateWrapper<K, V>
		extends TemporaryKeyValueTemplate<K, V>, TemporaryKeyTemplateWrapper<K>, KeyValueOperationsWrapper<K, V> {

	@Override
	TemporaryKeyValueTemplate<K, V> getSourceOperations();

	@Override
	default V getAndTouch(K key) {
		Encoder<K, K> fomatter = getKeyFomatter();
		return getSourceOperations().getAndTouch(fomatter == null ? key : fomatter.encode(key));
	}
}
