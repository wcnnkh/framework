package run.soeasy.framework.core.transform;

import run.soeasy.framework.core.collection.Dictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 映射
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface Mapping<K, V extends TypedValueAccessor> extends Dictionary<K, V, KeyValue<K, V>> {

	@Override
	default Mapping<K, V> asArray() {
		return this;
	}

	@Override
	default Mapping<K, V> asMap() {
		return new MapMapping<>(this);
	}
}
