package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
	public Object apply(@NonNull TypedValue value, @NonNull TargetDescriptor targetDescriptor)
			throws ConversionException {
		TypeDescriptor sourceType = value.getReturnTypeDescriptor();
		ConversionService conversionService = optional()
				.filter((e) -> e.canConvert(sourceType, targetDescriptor.getRequiredTypeDescriptor())).orElse(null);
		if (conversionService == null) {
			if (parentConversionService != null
					&& parentConversionService.canConvert(sourceType, targetDescriptor.getRequiredTypeDescriptor())) {
				return parentConversionService.apply(value, targetDescriptor);
			}
			throw new ConverterNotFoundException(value.getReturnTypeDescriptor(),
					targetDescriptor.getRequiredTypeDescriptor());
		}
		return conversionService.apply(value, targetDescriptor);
	}

}
