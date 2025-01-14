package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TemplateTransformFilter;
import io.basc.framework.core.convert.transform.TemplateTransformer;
import io.basc.framework.core.convert.transform.TransformContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FilterableTemplateTransformer<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		implements TemplateTransformer<K, SV, S, TV, T, E> {
	@NonNull
	private final Iterable<? extends TemplateTransformFilter<K, SV, S, TV, T, E>> filters;
	private TemplateTransformer<K, SV, S, TV, T, E> dottomlessTemplateTransformer;

	@Override
	public void transform(TransformContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, K index, SV accessor, TransformContext<K, TV, T> targetContext,
			@NonNull T target, @NonNull TypeDescriptor targetType) throws E {
		TemplateTransformerChain<K, SV, S, TV, T, E> chain = new TemplateTransformerChain<>(filters.iterator(),
				dottomlessTemplateTransformer);
		chain.transform(sourceContext, source, sourceType, index, accessor, targetContext, target, targetType);
	}
}
