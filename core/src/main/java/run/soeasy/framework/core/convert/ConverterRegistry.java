package run.soeasy.framework.core.convert;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;
import run.soeasy.framework.core.exchange.container.map.TreeMapContainer;

public class ConverterRegistry extends TreeMapContainer<TypeMapping, Converter> implements ConditionalConverter {
	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return getConverter(sourceTypeDescriptor, targetTypeDescriptor) != null;
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		Converter converter = getConverter(sourceTypeDescriptor, targetTypeDescriptor);
		if (converter == null) {
			throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
		}
		return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	private Converter getConverter(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		Converter converter = getConverterByHash(sourceTypeDescriptor, targetTypeDescriptor);
		if (converter == null) {
			converter = getConverterByHash(targetTypeDescriptor, sourceTypeDescriptor);
		}

		for (Entry<TypeMapping, Converter> entry : entrySet()) {
			if (entry.getValue().canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private Converter getConverterByHash(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		TypeMapping typeMapping = new TypeMapping(sourceTypeDescriptor.getType(), targetTypeDescriptor.getType());
		return get(typeMapping);
	}

	@Override
	public Set<TypeMapping> getConvertibleTypeMappings() {
		return keySet();
	}

	public final <S, T> Registration register(@NonNull Class<S> sourceClass, @NonNull Class<T> targetClass,
			Function<? super S, ? extends T> function) {
		FunctionConverter<S, T> converter = new FunctionConverter<>(sourceClass, targetClass, function);
		return register(converter);
	}

	public Registration register(@NonNull ConditionalConverter conditionalConverter) {
		Set<TypeMapping> typeMappings = conditionalConverter.getConvertibleTypeMappings();
		List<Registration> registrations = typeMappings.stream().map((e) -> register(e, conditionalConverter))
				.collect(Collectors.toList());
		return Registrations.forList(registrations);
	}
}
