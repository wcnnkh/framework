package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import lombok.NonNull;

@FunctionalInterface
public interface TemplateWriter<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable> {

	boolean writeTo(TemplateContext<K, SV, S> sourceContext, @NonNull S source, @NonNull TypeDescriptor sourceType,
			TemplateContext<K, TV, T> targetContext, @NonNull T target, @NonNull TypeDescriptor targetType, @NonNull K index,
			@NonNull SV sourceElement, @NonNull TV targetAccessor) throws E;
}