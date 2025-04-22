package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class TemplateWriteFilters<K, V extends ValueAccessor, T extends Template<K, V>>
		extends ConfigurableServices<TemplateWriteFilter<K, V, T>> implements TemplateWriteFilter<K, V, T> {

	@Override
	public boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext,
			@NonNull TemplateContext<K, V, T> targetContext, @NonNull TemplateWriter<K, V, T> writer) {
		ChainTemplateWriter<K, V, T> chain = new ChainTemplateWriter<>(this.iterator(), writer);
		return chain.writeTo(sourceContext, targetContext);
	}
}
