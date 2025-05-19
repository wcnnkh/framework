package run.soeasy.framework.core.transform.service;

import java.util.Map.Entry;
import java.util.TreeMap;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConvertiblePair;
import run.soeasy.framework.core.transform.Transformer;

public class TransformerRegistry implements TransformationService {
	private volatile TreeMap<ConvertiblePair, Transformer<Object, Object>> registry = new TreeMap<>();

	@SuppressWarnings("unchecked")
	public <S, T> void registerTransformer(Class<? extends S> sourceClass, Class<? extends T> targetClass,
			Transformer<S, T> transformer) {
		registry.put(new ConvertiblePair(sourceClass, targetClass), (Transformer<Object, Object>) transformer);
	}

	private Transformer<Object, Object> getTransformer(@NonNull TypeDescriptor sourceType,
			@NonNull TypeDescriptor targetType) {
		ConvertiblePair convertiblePair = new ConvertiblePair(sourceType.getType(), targetType.getType());
		Transformer<Object, Object> transformer = registry.get(convertiblePair);
		if (transformer != null) {
			if (transformer instanceof TransformationService) {
				if (((TransformationService) transformer).canTransform(sourceType, targetType)) {
					return transformer;
				}
			} else {
				return transformer;
			}
		}

		transformer = registry.get(convertiblePair.reversed());
		if (transformer != null && transformer instanceof ReversibleTransformer) {
			return ((ReversibleTransformer<Object, Object>) transformer).reversed();
		}

		for (Entry<ConvertiblePair, Transformer<Object, Object>> entry : registry.entrySet()) {
			if (entry.getKey().canConvert(sourceType, targetType)) {
				return entry.getValue();
			}

			if (entry.getValue() instanceof ReversibleTransformer
					&& entry.getKey().reversed().canConvert(sourceType, targetType)) {
				return ((ReversibleTransformer<Object, Object>) entry.getValue()).reversed();
			}
		}
		return null;
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return getTransformer(sourceType, targetType) != null;
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceType, @NonNull Object target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		Transformer<Object, Object> transformer = getTransformer(sourceType, targetType);
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return transformer.transform(source, sourceType, target, targetType);
	}

}
