package run.soeasy.framework.core.transform;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.KeyValueListable;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 映射
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface Mapping<K, V extends TypedValueAccessor> extends KeyValueListable<K, V, KeyValue<K, V>> {
	public static interface MappingWrapper<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
			extends Mapping<K, V>, KeyValueListableWrapper<K, V, KeyValue<K, V>, W> {
		@Override
		default Mapping<K, V> randomAccess() {
			return getSource().randomAccess();
		}
	}

	@Override
	default Mapping<K, V> randomAccess() {
		return new RandomAccessMapping<>(this);
	}
}
