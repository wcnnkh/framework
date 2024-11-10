package io.basc.framework.convert.transform;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Listable;

public interface Mapping<I extends KeyValue<K, V>, K, V extends Accessor> extends Listable<I> {

	/**
	 * 获取可以映射的存取器
	 * 
	 * @param key
	 * @return
	 */
	Elements<I> getElements(K key);
}
