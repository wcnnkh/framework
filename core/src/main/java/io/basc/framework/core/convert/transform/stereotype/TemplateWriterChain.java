package io.basc.framework.core.convert.transform.stereotype;

import java.util.Iterator;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TemplateWriterChain<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends SimpleTemplateWriter<K, SV, S, TV, T, E> {
	@NonNull
	private final Iterator<? extends TemplateWriteFilter<K, SV, S, TV, T, E>> iterator;
	private TemplateWriter<K, SV, S, TV, T, E> templateWriter;

	@Override
	public boolean writeTo(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull SV sourceElement, @NonNull TV targetAccessor)
			throws E {
		if (iterator.hasNext()) {
			return iterator.next().writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index,
					sourceElement, targetAccessor, this);
		} else if (templateWriter != null) {
			return templateWriter.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index,
					sourceElement, targetAccessor);
		}
		return super.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index, sourceElement,
				targetAccessor);
	}
}
