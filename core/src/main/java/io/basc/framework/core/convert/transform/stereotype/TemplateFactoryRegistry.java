package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.util.spi.ServiceMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class TemplateFactoryRegistry<S, K, V extends Value, T extends Template<K, ? extends V>>
		extends ServiceMap<TemplateFactory<S, K, V, T>> implements TemplateProvider<S, K, V, T> {
	private TemplateProvider<? super S, ? extends K, ? extends V, ? extends T> templateProvider;

	@Override
	public T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType) {
		TemplateFactory<? super S, ? extends K, ? extends V, ? extends T> templateFactory = search(
				requiredType.getType()).first();
		if (templateFactory == null) {
			return templateProvider.getTemplate(source, requiredType);
		}
		return templateFactory.getTemplate(source, requiredType);
	}

	@Override
	public boolean hasTemplate(@NonNull TypeDescriptor requiredType) {
		return !search(requiredType.getType()).isEmpty() || templateProvider.hasTemplate(requiredType);
	}
}
