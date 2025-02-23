package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import lombok.NonNull;

public interface TemplateProvider<S, K, V extends Source, T extends Template<K, ? extends V>>
		extends TemplateFactory<S, K, V, T> {
	boolean hasTemplate(@NonNull TypeDescriptor requiredType);
}
