package io.basc.framework.convert.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.register.Registration;

public class ConfigurableConversionService extends ConfigurableServices<ConversionService>
		implements ConversionService {
	private static final NestingChecker<ConversionService> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	public ConfigurableConversionService() {
		super(ConversionComparator.INSTANCE);
		setServiceClass(ConversionService.class);
		getServiceInjectors().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				ConversionServiceAware conversionServiceAware = (ConversionServiceAware) service;
				conversionServiceAware.setConversionService(this);
			}
			return Registration.EMPTY;
		});
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		for (ConversionService service : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return service.convert(source, sourceType, targetType);
				}
			} finally {
				registration.unregister();
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : getServices()) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return true;
				}
			} finally {
				registration.unregister();
			}
		}
		return false;
	}

}
