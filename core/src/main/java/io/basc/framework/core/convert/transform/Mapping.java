package io.basc.framework.core.convert.transform;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Listable;

public interface Mapping<K, V extends Access> extends Listable<KeyValue<K, V>> {
	public interface MappingWrapper<K, V extends Access, W extends Mapping<K, V>>
			extends Mapping<K, V>, ListableWrapper<KeyValue<K, V>, W> {
		@Override
		default Elements<V> getAccesses(K key) {
			return getSource().getAccesses(key);
		}
	}

	/**
	 * 用来获取对应的映射行为.
	 * 
	 * @param key
	 * @return
	 */
	Elements<V> getAccesses(K key);
}
