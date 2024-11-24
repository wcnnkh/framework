package io.basc.framework.core.convert.transform;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.ListableWrapper;

public interface MappingWrapper<K, V extends Access, W extends Mapping<K, V>>
		extends Mapping<K, V>, ListableWrapper<KeyValue<K, V>, W> {
	@Override
	default Elements<V> getAccesses(K key) {
		return getSource().getAccesses(key);
	}
}
