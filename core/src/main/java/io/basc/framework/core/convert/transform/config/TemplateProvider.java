package io.basc.framework.core.convert.transform.config;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TemplateFactory;
import lombok.NonNull;

public interface TemplateProvider<S, K, V extends Value, T extends Template<K, ? extends V>>
		extends TemplateFactory<S, K, V, T> {
	boolean hasTemplate(@NonNull TypeDescriptor requiredType);
}
