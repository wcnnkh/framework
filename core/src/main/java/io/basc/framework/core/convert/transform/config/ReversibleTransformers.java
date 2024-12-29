package io.basc.framework.core.convert.transform.config;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ReversibleTransformer;

public class ReversibleTransformers<S, T, E extends Throwable, R extends ReversibleTransformer<S, T, ? extends E>>
		extends Transformers<S, T, E, R> implements ReversibleTransformer<S, T, E> {
	@Override
	public boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return anyMatch((e) -> e.canReverseTransform(sourceType, targetType));
	}

	@Override
	public void reverseTransform(T source, TypeDescriptor sourceType, S target, TypeDescriptor targetType) throws E {
		for (R transform : this) {
			if (transform.canReverseTransform(sourceType, targetType)) {
				transform.reverseTransform(source, sourceType, target, targetType);
				return;
			}
		}
	}
}
