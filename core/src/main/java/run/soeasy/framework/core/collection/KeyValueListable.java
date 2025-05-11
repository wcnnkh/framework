package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.ObjectUtils;

@FunctionalInterface
public interface KeyValueListable<K, V, E extends KeyValue<K, V>> extends KeyValues<K, V>, Listable<E> {
	public static interface KeyValueListableWrapper<K, V, E extends KeyValue<K, V>, W extends KeyValueListable<K, V, E>>
			extends KeyValueListable<K, V, E>, KeyValuesWrapper<K, V, W>, ListableWrapper<E, W> {
		@Override
		default Elements<K> keys() {
			return getSource().keys();
		}

		@Override
		default Elements<V> getValues(K key) {
			return getSource().getValues(key);
		}

		@Override
		default boolean isRandomAccess() {
			return getSource().isRandomAccess();
		}

		@Override
		default KeyValueListable<K, V, E> randomAccess() {
			return getSource().randomAccess();
		}
	}

	@Override
	default Elements<K> keys() {
		return getElements().map((e) -> e.getKey());
	}

	@Override
	default Elements<V> getValues(K key) {
		return getElements().filter((e) -> ObjectUtils.equals(key, e.getKey())).map((e) -> e.getValue());
	}

	/**
	 * 是否随机访问
	 * 
	 * @return
	 */
	default boolean isRandomAccess() {
		return false;
	}

	default KeyValueListable<K, V, E> randomAccess() {
		return new RandomAccessKeyValues<>(this);
	}
}
