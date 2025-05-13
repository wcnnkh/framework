package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.RandomAccessKeyValues;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.transform.Mapping.MappingWrapper;

public class RandomAccessMapping<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
		extends RandomAccessKeyValues<K, V, KeyValue<K, V>, W> implements MappingWrapper<K, V, W> {

	public RandomAccessMapping(@NonNull W source) {
		super(source);
	}

	@Override
	public Mapping<K, V> randomAccess() {
		return this;
	}
}
