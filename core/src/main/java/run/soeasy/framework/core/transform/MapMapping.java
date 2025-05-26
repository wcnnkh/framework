package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.collection.MapDictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

public class MapMapping<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
		extends MapDictionary<K, V, KeyValue<K, V>, W> implements MappingWrapper<K, V, W> {

	public MapMapping(@NonNull W source) {
		super(source);
	}

	@Override
	public Mapping<K, V> asArray() {
		return getSource();
	}

	@Override
	public Mapping<K, V> asMap() {
		return this;
	}
}
