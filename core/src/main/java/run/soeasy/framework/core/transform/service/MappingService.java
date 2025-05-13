package run.soeasy.framework.core.transform.service;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.transform.ObjectTransformer;
import run.soeasy.framework.core.transform.indexed.IndexedAccessor;
import run.soeasy.framework.core.transform.indexed.IndexedMapping;

@Getter
public class MappingService<T extends IndexedAccessor> extends ObjectTransformer<Object, T, IndexedMapping<T>>
		implements ConversionService {
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
}
