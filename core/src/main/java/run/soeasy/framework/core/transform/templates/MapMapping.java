package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.MapDictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

public class MapMapping<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
		extends MapDictionary<K, V, KeyValue<K, V>, W> implements MappingWrapper<K, V, W> {

	public MapMapping(W source, boolean orderly, boolean uniqueMapping) {
		super(source, orderly, uniqueMapping);
	}

	@Override
	public Mapping<K, V> asMap(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
	}
}
