package io.basc.framework.core.convert.transform;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Listable;

public interface Mapping<K, V extends Access> extends Listable<KeyValue<K, V>> {

	/**
	 * 用来获取对应的映射行为.
	 * 
	 * @param key
	 * @return
	 */
	Elements<V> getAccesses(K key);
}
