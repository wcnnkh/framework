package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import lombok.NonNull;

public interface TemplateFactory<S, K, V extends Value, T extends Template<K, ? extends V>> {
	T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
