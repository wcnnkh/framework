package run.soeasy.framework.core.convert.service;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceProvider;

public class ConifgurableConversionService extends ServiceProvider<ConversionService, ConversionException>
		implements ConversionService {
	public ConifgurableConversionService() {
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

	@Getter
	@Setter
	private volatile ConversionService parentConversionService;

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return optional().filter((e) -> e.canConvert(sourceType, targetType)).isPresent()
				|| (parentConversionService != null && parentConversionService.canConvert(sourceType, targetType));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		ConversionService conversionService = optional().filter((e) -> e.canConvert(sourceType, targetType))
				.orElse(null);
		if (conversionService == null) {
			if (parentConversionService != null && parentConversionService.canConvert(sourceType, targetType)) {
				return parentConversionService.convert(source, sourceType, targetType);
			}
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return conversionService.convert(source, sourceType, targetType);
	}

}
