package run.soeasy.framework.core.transform.stereotype;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ServiceMap;

@Getter
@Setter
public class TemplateFactoryRegistry<S, K, V extends Source, T extends Template<K, ? extends V>>
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
