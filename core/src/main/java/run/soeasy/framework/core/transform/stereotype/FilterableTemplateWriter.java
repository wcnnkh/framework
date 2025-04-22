package run.soeasy.framework.core.transform.stereotype;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

@RequiredArgsConstructor
@Getter
public class FilterableTemplateWriter<K, V extends ValueAccessor, T extends Template<K, V>>
		implements TemplateWriter<K, V, T> {
	@NonNull
	private final Iterable<TemplateWriteFilter<K, V, T>> filters;
	@NonNull
	private final TemplateWriter<K, V, T> templateWriter;

	@Override
	public boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext,
			@NonNull TemplateContext<K, V, T> targetContext) {
		ChainTemplateWriter<K, V, T> chain = new ChainTemplateWriter<>(filters.iterator(), templateWriter);
		return chain.writeTo(sourceContext, targetContext);
	}

	public final boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext,
			@NonNull TemplateContext<K, V, T> targetContext, @NonNull Iterable<TemplateWriteFilter<K, V, T>> filters) {
		FilterableTemplateWriter<K, V, T> templateWriter = new FilterableTemplateWriter<>(filters, this);
		return templateWriter.writeTo(sourceContext, targetContext);
	}
}
