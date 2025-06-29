package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.Dictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 映射
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface Mapping<K, V extends TypedValueAccessor> extends Dictionary<K, V, KeyValue<K, V>> {

	@Override
	default Mapping<K, V> asMap(boolean uniqueness) {
		return new MapMapping<>(this, true, uniqueness);
	}

	@Override
	default Mapping<K, V> asArray(boolean uniqueness) {
		return new ArrayMapping<>(this, uniqueness);
	}

}
