package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TransformContext;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class TemplateTransformerStractegy<E extends Throwable> extends
		DefaultTemplateTransformer<Object, Value, Template<Object, ? extends Value>, Accessor, Template<Object, ? extends Accessor>, E> {
	@Override
	protected Elements<? extends Accessor> getTargetAccessors(Object index, Value accessor,
			TransformContext<Object, Accessor, Template<Object, ? extends Accessor>> targetContext,
			@NonNull Template<Object, ? extends Accessor> target, @NonNull TypeDescriptor targetType) {
		// TODO Auto-generated method stub
		return super.getTargetAccessors(index, accessor, targetContext, target, targetType);
	}
}
