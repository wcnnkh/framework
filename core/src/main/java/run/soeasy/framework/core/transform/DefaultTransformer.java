package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

@Getter
public class DefaultTransformer<K, V extends TypedValueAccessor, R extends Mapping<K, V>, S, T>
		extends DefaultMapper<K, V, R> implements Transformer<S, T, ConversionException> {
	private final MappingRegistry<S, K, V, R> sourceMappingRegistry = new MappingRegistry<>();
	private final MappingRegistry<T, K, V, R> targetMappingRegistry = new MappingRegistry<>();

	protected R getSourceMapping(@NonNull S source, @NonNull TypeDescriptor sourceType) {
		return sourceMappingRegistry.getMapping(source, sourceType);
	}

	protected R getTargetMapping(@NonNull T target, @NonNull TypeDescriptor targetType) {
		return targetMappingRegistry.getMapping(target, targetType);
	}

	protected boolean hasSourceMapping(@NonNull TypeDescriptor requiredType) {
		return sourceMappingRegistry.hasMapping(requiredType);
	}

	protected boolean hasTargetMapping(@NonNull TypeDescriptor requiredType) {
		return targetMappingRegistry.hasMapping(requiredType);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return hasSourceMapping(sourceType) && hasTargetMapping(targetType);
	}

	@Override
	public boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		R sourceTemplate = getSourceMapping(source, sourceType);
		R targetTemplate = getTargetMapping(target, targetType);
		return doMapping(new MappingContext<>(sourceTemplate), new MappingContext<>(targetTemplate));
	}
}
