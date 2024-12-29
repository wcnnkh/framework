package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public interface TemplateTransformer<K, SV extends Value, S extends Template<K, SV>, TV extends Accessor, T extends Template<K, TV>, E extends Throwable>
		extends Transformer<S, T, E> {
	@Override
	default void transform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		transform(null, source, sourceType, null, target, targetType);
	}

	default void transform(TransformContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TransformContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E {
		for (K index : source.getAccessorIndexes()) {
			Elements<SV> accessors = source.getAccessors(index);
			for (SV accessor : accessors) {
				transform(sourceContext, source, sourceType, index, accessor, targetContext, target, targetType);
			}
		}
	}

	void transform(TransformContext<K, SV, S> sourceContext, @NonNull S source, @NonNull TypeDescriptor sourceType,
			K index, SV accessor, TransformContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E;
}