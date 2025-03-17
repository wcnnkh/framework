package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class FilterableTemplateWriter<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends SimpleTemplateWriter<K, SV, S, TV, T, E> {
	@NonNull
	private final Iterable<? extends TemplateWriteFilter<K, SV, S, TV, T, E>> templateWriteFilters;
	private TemplateWriter<K, SV, S, TV, T, E> templateWriter;

	@Override
	public boolean writeTo(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull SV sourceElement, @NonNull TV targetAccessor)
			throws E {
		TemplateWriterChain<K, SV, S, TV, T, E> chain = new TemplateWriterChain<>(templateWriteFilters.iterator(),
				templateWriter);
		if (chain.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index, sourceElement,
				targetAccessor)) {
			return true;
		}
		return super.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index, sourceElement,
				targetAccessor);
	}
}
