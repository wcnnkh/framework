package run.soeasy.framework.beans;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.transform.property.PropertyMappingFilter;
import run.soeasy.framework.core.transform.property.PropertyMappingService;

public class BeanUtils {
	private static final BeanTemplateFactory TEMPLATE_FACTORY = new BeanTemplateFactory();

	public static BeanTemplateFactory getTemplateFactory() {
		return TEMPLATE_FACTORY;
	}

	public static PropertyMappingService createMapper() {
		PropertyMappingService mappingService = new PropertyMappingService();
		mappingService.getMappingRegistry().setMappingProvider(TEMPLATE_FACTORY);
		return mappingService;
	}

	public static <S, T> boolean copyProperties(@NonNull S source, @NonNull T target,
			@NonNull PropertyMappingFilter... filters) {
		return copyProperties(source, source.getClass(), target, target.getClass(), filters);
	}

	public static <S, T> boolean copyProperties(S source, @NonNull Class<? extends S> sourceClass, T target,
			@NonNull Class<? extends T> targetClass, @NonNull PropertyMappingFilter... filters) {
		PropertyMappingService mappingService = createMapper();
		mappingService.getFilters().registers(Elements.forArray(filters));
		return mappingService.transform(source, sourceClass, target, targetClass);
	}
}
