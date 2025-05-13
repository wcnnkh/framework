package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

@FunctionalInterface
public interface MappingFactory<S, K, V extends TypedValueAccessor, T extends Mapping<K, V>> {
	T getMapping(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
