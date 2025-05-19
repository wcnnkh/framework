package run.soeasy.framework.core.transform.service;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.transform.Mapping;

@FunctionalInterface
public interface MappingFactory<S, K, V extends TypedValueAccessor, T extends Mapping<K, V>> {
	T getMapping(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
