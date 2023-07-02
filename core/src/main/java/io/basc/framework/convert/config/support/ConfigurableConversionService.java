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
		getServiceInjectorRegistry().register((service) -> {
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

		if (isInverterRegistred(sourceType.getType())) {
			return super.invert(source, sourceType, targetType);
		}

		if (canInstantiated(targetType) && isTransformerRegistred(sourceTypeToUse.getType())) {
			Object target = newInstance(targetType);
			super.reverseTransform(source, sourceTypeToUse, target, targetType);
			return target;
		}
		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	@Override
	public final <R> R invert(Object source, Class<? extends Object> sourceType, Class<? extends R> targetType)
			throws ConversionException {
		return convert(source, sourceType, targetType);
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
