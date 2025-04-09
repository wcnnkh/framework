package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.spi.ServiceProvider;

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
	public Object convert(@NonNull Source value, @NonNull TypeDescriptor targetType) throws ConversionException {
		TypeDescriptor sourceType = value.getTypeDescriptor();
		ConversionService conversionService = optional().filter((e) -> e.canConvert(sourceType, targetType))
				.orElse(null);
		if (conversionService == null) {
			if (parentConversionService != null && parentConversionService.canConvert(sourceType, targetType)) {
				return parentConversionService.convert(value, targetType);
			}
			throw new ConverterNotFoundException(value.getTypeDescriptor(), targetType);
		}
		return conversionService.convert(value, targetType);
	}

}
