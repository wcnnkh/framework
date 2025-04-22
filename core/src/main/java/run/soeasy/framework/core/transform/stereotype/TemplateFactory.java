package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

@FunctionalInterface
public interface TemplateFactory<S, K, V extends ValueAccessor, T extends Template<K, V>> {
	T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
