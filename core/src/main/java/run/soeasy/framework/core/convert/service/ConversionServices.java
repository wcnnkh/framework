package run.soeasy.framework.core.convert.service;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.check.NestingChecker;
import run.soeasy.framework.util.check.ThreadLocalNestingChecker;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.spi.Providers;

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
