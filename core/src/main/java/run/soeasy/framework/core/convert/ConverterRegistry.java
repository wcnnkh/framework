package run.soeasy.framework.core.convert;

import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.map.TreeMapContainer;
import run.soeasy.framework.core.spi.ServiceInjectors;
import run.soeasy.framework.core.type.TypeMapping;

@Getter
public class ConverterRegistry implements ConditionalConversionService {
	private final ServiceInjectors<Converter<?, ?>> injectors = new ServiceInjectors<>();
	private final Converters converters = new Converters();
	private final TreeMapContainer<TypeMapping, CustomizeConditionalConversionService> registry = new TreeMapContainer<>();

	public ConverterRegistry() {
		converters.getInjectors().add(injectors);
		injectors.register((service) -> {
			if (service instanceof ConversionServiceAware) {
				ConversionServiceAware conversionServiceAware = (ConversionServiceAware) service;
				conversionServiceAware.setConversionService(this);
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return getConversionService(sourceTypeDescriptor, targetTypeDescriptor) != null
				|| converters.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		ConversionService conversionService = getConversionService(sourceType, targetType);
		if (conversionService != null) {
			return conversionService.convert(source, sourceType, targetType);
		}
		return converters.convert(source, sourceType, targetType);
	}

	private <S, T> ConversionService getConversionService(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		ConversionService conversionService = getConversionServiceByHash(sourceTypeDescriptor, targetTypeDescriptor);
		if (conversionService == null) {
			conversionService = getConversionServiceByHash(targetTypeDescriptor, sourceTypeDescriptor);
		}

		for (Entry<TypeMapping, CustomizeConditionalConversionService> entry : registry.entrySet()) {
			if (entry.getValue().canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				return entry.getValue();
			} else if (entry.getValue().canConvert(targetTypeDescriptor, sourceTypeDescriptor)) {
				return entry.getValue().reversed();
			}
		}
		return null;
	}

	private ConversionService getConversionServiceByHash(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		TypeMapping typeMapping = new TypeMapping(sourceTypeDescriptor.getType(), targetTypeDescriptor.getType());
		CustomizeConditionalConversionService converter = registry.get(typeMapping);
		if (converter != null) {
			if (converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				return converter;
			} else if (converter.canConvert(targetTypeDescriptor, sourceTypeDescriptor)) {
				return converter.reversed();
			}
		}
		return null;
	}

	@Override
	public Set<TypeMapping> getConvertibleTypeMappings() {
		return Stream.concat(registry.values().stream().flatMap((e) -> e.getConvertibleTypeMappings().stream()),
				converters.getConvertibleTypeMappings().stream()).collect(Collectors.toSet());
	}

	public final <S, T> Registration registerConverter(@NonNull Class<S> sourceClass, @NonNull Class<T> targetClass,
			Converter<? super S, ? extends T> converter, Converter<? super T, ? extends S> reversedConverter) {
		ReversedConverter<S, T> reversibleConverter = new ReversedConverter<>(converter, reversedConverter);
		Registration registration = registerReversibleConverter(sourceClass, targetClass, reversibleConverter);
		if (!registration.isCancelled()) {
			if (converter != null) {
				injectors.inject(converter);
			}

			if (reversedConverter != null) {
				injectors.inject(reversedConverter);
			}
		}
		return registration;
	}

	public final <S, T> Registration registerReversibleConverter(@NonNull Class<S> sourceClass,
			@NonNull Class<T> targetClass, @NonNull ReversibleConverter<S, T> reversibleConverter) {
		return registerReversibleConverter(new TypeMapping(sourceClass, targetClass), reversibleConverter);
	}

	public <S, T> Registration registerReversibleConverter(TypeMapping typeMapping,
			@NonNull ReversibleConverter<S, T> reversibleConverter) {
		Registration registration = registry.register(typeMapping,
				new CustomizeConditionalConversionService(typeMapping, reversibleConverter));
		if (!registration.isCancelled()) {
			injectors.inject(reversibleConverter);
		}
		return registration;
	}

}
