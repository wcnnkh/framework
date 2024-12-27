package io.basc.framework.core.convert.transform;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;

public interface Mapping<K, V extends Accesstor> {

	public interface MappingWrapper<K, V extends Accesstor, W extends Mapping<K, V>> extends Mapping<K, V>, Wrapper<W> {
		@Override
		default Elements<V> getAccesstors(K key) {
			return getSource().getAccesstors(key);
		}

		@Override
		default Elements<KeyValue<K, V>> getMembers() {
			return getSource().getMembers();
		}
	}

	/**
	 * 获取映射的成员
	 * 
	 * @return
	 */
	Elements<KeyValue<K, V>> getMembers();
	
	/**
	 * 获取映射的索引对象
	 * @return
	 */
	//Elements<K> getIndexes();

	/**
	 * 用来获取对应的映射行为.
	 * 
	 * @param key
	 * @return
	 */
	Elements<V> getAccesstors(@NonNull K key);
}
