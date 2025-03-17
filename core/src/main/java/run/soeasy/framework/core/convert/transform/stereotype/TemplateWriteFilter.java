package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface TemplateWriteFilter<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable> {

	boolean writeTo(TemplateContext<K, SV, S> sourceContext, @NonNull S source, @NonNull TypeDescriptor sourceType,
			TemplateContext<K, TV, T> targetContext, @NonNull T target, @NonNull TypeDescriptor targetType, @NonNull K index,
			@NonNull SV sourceElement, @NonNull TV targetAccessor, @NonNull TemplateWriter<K, SV, S, TV, T, E> writer) throws E;

}
