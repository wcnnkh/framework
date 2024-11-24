package io.basc.framework.core.convert.config;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableConversionService extends ConfigurableServices<ConversionService>
		implements ConversionService {
	private static final NestingChecker<ConversionService> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	public ConfigurableConversionService() {
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
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		for (ConversionService service : this) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return service.convert(source, sourceType, targetType);
				}
			} finally {
				registration.cancel();
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
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

}
