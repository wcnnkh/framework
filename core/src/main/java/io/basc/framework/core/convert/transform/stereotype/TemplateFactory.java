package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import lombok.NonNull;

@FunctionalInterface
public interface TemplateFactory<S, K, V extends Source, T extends Template<K, ? extends V>> {
	T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
