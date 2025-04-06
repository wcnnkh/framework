package run.soeasy.framework.core.transform.config;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.ReversibleTransformer;
import run.soeasy.framework.core.transform.Transformer;

public interface ReversibleTransformerFactory<S, E extends Throwable>
		extends ReversibleTransformer<S, Object, E>, TransformerFactory<S, E> {
	<T> ReversibleTransformer<S, T, E> getReversibleTransformer(@NonNull Class<? extends T> requiredType);

	@Override
	default <T> Transformer<S, T, E> getTransformer(@NonNull Class<? extends T> requiredType) {
		return getReversibleTransformer(requiredType);
	}

	@Override
	default boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getReversibleTransformer(sourceType.getType()) != null;
	}

	default void reverseTransform(@NonNull Object source, @NonNull S target) throws E {
		reverseTransform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		ReversibleTransformer<S, Object, E> reversibleTransformer = getReversibleTransformer(sourceType.getType());
		if (reversibleTransformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		reversibleTransformer.reverseTransform(source, sourceType, target, targetType);
	}
}
