package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValueAccessor;

@FunctionalInterface
public interface TemplateFactory<S, K, V extends TypedValueAccessor, T extends Template<K, V>> {
	T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
