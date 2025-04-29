package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

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
	default boolean reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		ReversibleTransformer<S, Object, E> reversibleTransformer = getReversibleTransformer(sourceType.getType());
		if (reversibleTransformer == null) {
			return false;
		}
		return reversibleTransformer.reverseTransform(source, sourceType, target, targetType);
	}
}
