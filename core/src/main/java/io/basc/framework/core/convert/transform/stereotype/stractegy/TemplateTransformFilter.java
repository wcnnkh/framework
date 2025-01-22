package io.basc.framework.core.convert.transform.stereotype.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.TemplateTransformer;
import io.basc.framework.core.convert.transform.stereotype.TransformContext;
import lombok.NonNull;

public interface TemplateTransformFilter<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable> {
	void transform(TransformContext<K, SV, S> sourceContext, @NonNull S source, @NonNull TypeDescriptor sourceType,
			K index, SV accessor, TransformContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull TemplateTransformer<K, SV, S, TV, T, E> templateTransformer)
			throws E;
}
