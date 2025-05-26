package run.soeasy.framework.core.transform;

import run.soeasy.framework.core.collection.DictionaryWrapper;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

public interface MappingWrapper<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
		extends Mapping<K, V>, DictionaryWrapper<K, V, KeyValue<K, V>, W> {
	@Override
	default Mapping<K, V> asArray() {
		return getSource().asArray();
	}

	@Override
	default Mapping<K, V> asMap() {
		return getSource().asMap();
	}
}