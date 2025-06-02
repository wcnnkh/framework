package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.ArrayDictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

public class ArrayMapping<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
		extends ArrayDictionary<K, V, KeyValue<K, V>, W> implements MappingWrapper<K, V, W> {

	public ArrayMapping(W source, boolean uniqueMapping) {
		super(source, uniqueMapping);
	}

	@Override
	public Mapping<K, V> asArray(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
	}
}
