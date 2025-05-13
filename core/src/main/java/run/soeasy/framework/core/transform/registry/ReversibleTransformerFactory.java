package run.soeasy.framework.core.transform.registry;

import lombok.NonNull;
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
