package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import lombok.NonNull;

@FunctionalInterface
public interface TemplateFactory<S, K, V extends Value, T extends Template<K, ? extends V>> {
	T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
