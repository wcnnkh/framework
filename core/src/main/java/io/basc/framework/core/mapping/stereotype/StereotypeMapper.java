package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.mapping.Mapper;
import io.basc.framework.core.mapping.factory.ConfigurableInstanceFactory;
import io.basc.framework.core.mapping.factory.DefaultMapperRegistry;
import lombok.NonNull;

public class StereotypeMapper
		extends StereotypeTransformer<FieldDescriptor, FieldDescriptorTemplate<FieldDescriptor>, ConversionException>
		implements Mapper<Object, Object, ConversionException> {
	private final ConfigurableInstanceFactory instanceFactory = new ConfigurableInstanceFactory();
	private final DefaultMapperRegistry<Object, ConversionException> mapperRegistry = new DefaultMapperRegistry<>();

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return mapperRegistry.canConvert(sourceType, targetType) || Mapper.super.canConvert(sourceType, targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException, ConversionFailedException {
		if (mapperRegistry.canConvert(sourceType, targetType)) {
			return mapperRegistry.convert(source, sourceType, targetType);
		}
		return Mapper.super.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return mapperRegistry.canTransform(sourceType, targetType) || super.canTransform(sourceType, targetType);
	}

	@Override
	public void transform(@NonNull Object source, @NonNull TypeDescriptor sourceType, @NonNull Object target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		if (mapperRegistry.canTransform(sourceType, targetType)) {
			mapperRegistry.transform(source, sourceType, target, targetType);
			return;
		}
		super.transform(source, sourceType, target, targetType);
	}
}
