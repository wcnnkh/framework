package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Transformer;
import lombok.NonNull;

@FunctionalInterface
public interface TemplateTransformer<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends Transformer<S, T, E> {
	@Override
	default void transform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		for (K index : target.getAccessorIndexes()) {
			transform(source, sourceType, target, targetType, index);
		}
	}

	void transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType, K index) throws E;
}