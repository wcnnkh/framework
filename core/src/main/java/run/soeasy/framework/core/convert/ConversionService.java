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
				ConverterAware converterAware = (ConverterAware) service;
				converterAware.setConverter(this);
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

	@Override
	public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return Converter.super.canConvert(sourceClass, targetClass);
	}

	@Override
	public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return Converter.super.canConvert(sourceClass, targetTypeDescriptor);
	}

	@Override
	public final boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return Converter.super.canConvert(sourceTypeDescriptor, targetClass);
	}

	@Override
	public final <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass)
			throws ConversionException {
		return Converter.super.convert(source, targetClass);
	}

	@Override
	public final Object convert(Object source, @NonNull Class<?> sourceClass,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return Converter.super.convert(source, sourceClass, targetTypeDescriptor);
	}

	@Override
	public final <T> T convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Class<? extends T> targetClass) throws ConversionException {
		return Converter.super.convert(source, sourceTypeDescriptor, targetClass);
	}

	@Override
	public final Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return Converter.super.convert(source, targetTypeDescriptor);
	}
}
