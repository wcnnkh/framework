package run.soeasy.framework.core.transform.mapping;

import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.ReversibleTransformer;

public interface ReversibleMapper<S, T, E extends Throwable>
		extends Mapper<S, T, E>, ReversibleConverter<S, T, E>, ReversibleTransformer<S, T, E> {
	@Override
	default boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return canInstantiated(targetType) && canReverseTransform(sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	@Override
	default S reverseConvert(T source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E, ConversionFailedException {
		if (this.canReverseConvert(sourceType, targetType)) {
			S target = (S) newInstance(targetType);
			reverseTransform(source, sourceType, target, targetType);
			return target;
		}
		throw new ConversionFailedException(sourceType, targetType, source, null);
	}
}
