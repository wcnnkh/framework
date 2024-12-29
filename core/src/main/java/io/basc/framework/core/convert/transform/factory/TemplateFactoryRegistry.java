package io.basc.framework.core.convert.transform.factory;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TemplateFactory;
import io.basc.framework.util.spi.ServiceMap;
import lombok.NonNull;

public class TemplateFactoryRegistry<S, K, V extends Value, T extends Template<K, V>>
		extends ServiceMap<TemplateFactory<? super S, K, V, T>> implements TemplateFactory<S, K, V, T> {

	@Override
	public T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType) {
		TemplateFactory<? super S, K, V, T> templateFactory = getFirst(requiredType.getType());
		if (templateFactory == null) {
			return null;
		}
		return templateFactory.getTemplate(source, requiredType);
	}

	public boolean containsTemplate(@NonNull Class<?> requiredType) {
		return getFirst(requiredType) != null;
	}
}
