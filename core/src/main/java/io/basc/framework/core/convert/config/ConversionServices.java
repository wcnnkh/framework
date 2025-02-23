package io.basc.framework.core.convert.config;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.spi.Providers;
import lombok.NonNull;

public class ConversionServices extends Providers<ConversionService, ConversionException> implements ConversionService {
	private static final NestingChecker<ConversionService> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();

	public ConversionServices() {
		setNestingChecker(NESTING_CHECKERS);
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
		return optional().filter((e) -> e.canConvert(sourceType, targetType)).isPresent();
	}

	@Override
	public Object convert(@NonNull Source value, @NonNull TypeDescriptor targetType) throws ConversionException {
		ConversionService conversionService = optional().filter((e) -> e.canConvert(targetType, targetType))
				.orElse(null);
		if (conversionService == null) {
			throw new ConverterNotFoundException(value.getTypeDescriptor(), targetType);
		}
		return conversionService.convert(value, targetType);
	}

}
