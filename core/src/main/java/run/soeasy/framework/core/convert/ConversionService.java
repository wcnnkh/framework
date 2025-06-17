package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceInjectors;

@Getter
public class ConversionService implements Converter {
	private final Converters converters = new Converters();
	private final ServiceInjectors<Object> injectors = new ServiceInjectors<>();
	private final ConverterRegistry registry = new ConverterRegistry();

	public ConversionService() {
		injectors.register((service) -> {
			if (service instanceof ConverterAware) {
				ConverterAware conversionServiceAware = (ConverterAware) service;
				conversionServiceAware.setConverter(this);
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return registry.canConvert(sourceTypeDescriptor, targetTypeDescriptor)
				|| converters.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (registry.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return registry.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}
		return converters.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	public Registration register(@NonNull Converter converter) {
		Registration registration = converter instanceof ConditionalConverter
				? registry.register((ConditionalConverter) converter)
				: converters.register(converter);
		if (!registration.isCancelled()) {
			injectors.inject(converter);
		}
		return registration;
	}
}
