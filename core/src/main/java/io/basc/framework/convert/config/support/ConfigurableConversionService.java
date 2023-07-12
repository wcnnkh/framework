package io.basc.framework.convert.config.support;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionComparator;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.util.Registration;

public class ConfigurableConversionService extends DefaultMapperRegistry<Object, ConversionException>
		implements ConversionService, Comparable<Object> {
	public ConfigurableConversionService() {
		getServiceInjectors().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				ConversionServiceAware conversionServiceAware = (ConversionServiceAware) service;
				conversionServiceAware.setConversionService(this);
			}
			return Registration.EMPTY;
		});
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (canInstantiated(targetType)) {
			if (isTransformerRegistred(targetType.getType())) {
				return true;
			}

			if (sourceType != null && isReverseTransformerRegistred(sourceType.getType())) {
				return true;
			}
		}
		return super.canConvert(sourceType, targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		TypeDescriptor sourceTypeToUse = sourceType == null ? TypeDescriptor.forObject(source) : sourceType;
		if (super.canConvert(sourceTypeToUse, targetType)) {
			return super.convert(source, sourceTypeToUse, targetType);
		}

		if (isInverterRegistred(sourceTypeToUse.getType())) {
			return super.invert(source, sourceTypeToUse, targetType);
		}

		if (canInstantiated(targetType) && canTransform(sourceTypeToUse, targetType)) {
			Object target = newInstance(targetType);
			transform(source, sourceTypeToUse, target, targetType);
			return target;
		}
		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws ConversionException, ConverterNotFoundException {
		TypeDescriptor sourceTypeToUse = sourceType == null ? TypeDescriptor.forObject(source) : sourceType;
		if (isTransformerRegistred(targetType.getType())) {
			super.transform(source, sourceTypeToUse, target, targetType);
		} else if (isReverseTransformerRegistred(sourceTypeToUse.getType())) {
			super.reverseTransform(target, targetType, source, sourceTypeToUse);
		}
		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	@Override
	public boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canTransform(sourceType, targetType)
				|| (sourceType != null && isReverseTransformerRegistred(sourceType.getType()));
	}

	@Override
	public final void reverseTransform(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType) throws ConversionException {
		TypeDescriptor sourceTypeToUse = sourceType == null ? TypeDescriptor.forObject(source) : sourceType;
		if (isReverseTransformerRegistred(sourceTypeToUse.getType())) {
			super.reverseTransform(source, sourceType, target, targetType);
			return;
		}

		if (canTransform(targetType, sourceTypeToUse)) {
			transform(target, targetType, source, sourceTypeToUse);
		}
		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	@Override
	public final Object invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		TypeDescriptor sourceTypeToUse = sourceType == null ? TypeDescriptor.forObject(source) : sourceType;
		if (isInverterRegistred(sourceTypeToUse.getType())) {
			return super.invert(source, sourceTypeToUse, targetType);
		}

		if (canConvert(sourceTypeToUse, targetType)) {
			return convert(source, sourceTypeToUse);
		}
		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	@Override
	public int compareTo(Object o) {
		for (ConversionService service : getServices()) {
			if (ConversionComparator.INSTANCE.compare(service, o) == -1) {
				return -1;
			}
		}
		return 1;
	}
}
