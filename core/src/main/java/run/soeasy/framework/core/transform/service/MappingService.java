package run.soeasy.framework.core.transform.service;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.transform.DefaultMapper;
import run.soeasy.framework.core.transform.Mapping;
import run.soeasy.framework.core.transform.MappingContext;

@Getter
public class MappingService<K, V extends TypedValueAccessor, R extends Mapping<K, V>> extends DefaultMapper<K, V, R>
		implements TransformationService, ConversionService {
	private final MappingRegistry<K, V, R> mappingRegistry = new MappingRegistry<>();
	private final ConfigurableInstanceFactory configurableInstanceFactory = new ConfigurableInstanceFactory();

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return configurableInstanceFactory.canInstantiated(targetType.getResolvableType())
				&& canTransform(sourceType, targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Object target = configurableInstanceFactory.newInstance(targetType.getResolvableType());
		transform(source, sourceType, target, targetType);
		return target;
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return mappingRegistry.hasMapping(sourceType) && mappingRegistry.hasMapping(targetType);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceType, @NonNull Object target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		R sourceTemplate = mappingRegistry.getMapping(source, sourceType);
		R targetTemplate = mappingRegistry.getMapping(target, targetType);
		return doMapping(new MappingContext<>(sourceTemplate), new MappingContext<>(targetTemplate));
	}
}
