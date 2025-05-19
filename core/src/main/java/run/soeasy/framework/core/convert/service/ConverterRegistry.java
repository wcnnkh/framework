package run.soeasy.framework.core.convert.service;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class ConverterRegistry implements ConditionalConversionService {
	private volatile TreeMap<ConvertiblePair, Converter<Object, Object>> registry = new TreeMap<>();

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return registry.entrySet().stream().flatMap((entry) -> {
			if (entry.getValue() instanceof ConvertibleConditional) {
				return ((ConvertibleConditional) entry.getValue()).getConvertibleTypes().stream();
			} else if (entry.getValue() instanceof ReversibleConverter) {
				return Stream.of(entry.getKey(), entry.getKey().reversed());
			} else {
				return Stream.of(entry.getKey());
			}
		}).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	public <S, T> void registerConverter(Class<? extends S> sourceClass, Class<? extends T> targetClass,
			Converter<S, T> converter) {
		registry.put(new ConvertiblePair(sourceClass, targetClass), (Converter<Object, Object>) converter);
	}

	private Converter<Object, Object> getConverter(@NonNull TypeDescriptor sourceType,
			@NonNull TypeDescriptor targetType) {
		ConvertiblePair convertiblePair = new ConvertiblePair(sourceType.getType(), targetType.getType());
		Converter<Object, Object> converter = registry.get(convertiblePair);
		if (converter != null) {
			if (converter instanceof ConversionService) {
				if (((ConversionService) converter).canConvert(sourceType, targetType)) {
					return converter;
				}
			} else {
				return converter;
			}
		}

		converter = registry.get(convertiblePair.reversed());
		if (converter != null && converter instanceof ReversibleConverter) {
			return ((ReversibleConverter<Object, Object>) converter).reversed();
		}

		for (Entry<ConvertiblePair, Converter<Object, Object>> entry : registry.entrySet()) {
			if (entry.getKey().canConvert(sourceType, targetType)) {
				return entry.getValue();
			}

			if (entry.getValue() instanceof ReversibleConverter
					&& entry.getKey().reversed().canConvert(sourceType, targetType)) {
				return ((ReversibleConverter<Object, Object>) entry.getValue()).reversed();
			}
		}
		return null;
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return getConverter(sourceType, targetType) != null;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Converter<Object, Object> converter = getConverter(sourceType, targetType);
		if (converter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return converter.convert(source, sourceType, targetType);
	}

}
