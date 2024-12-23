package io.basc.framework.core.convert.service;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.util.Registration;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class ConversionServices extends ConfigurableServices<ConversionService> implements ConversionService {
	private static final NestingChecker<ConversionService> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	public ConversionServices() {
		setComparator(ConversionComparator.INSTANCE);
		setServiceClass(ConversionService.class);
		getInjectors().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				ConversionServiceAware conversionServiceAware = (ConversionServiceAware) service;
				conversionServiceAware.setConversionService(this);
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : this) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return true;
				}
			} finally {
				registration.cancel();
			}
		}
		return false;
	}

	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		for (ConversionService service : this) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(value.getTypeDescriptor(), targetType)) {
					return service.convert(value, targetType);
				}
			} finally {
				registration.cancel();
			}
		}
		throw new ConverterNotFoundException(value.getTypeDescriptor(), targetType);
	}

}
