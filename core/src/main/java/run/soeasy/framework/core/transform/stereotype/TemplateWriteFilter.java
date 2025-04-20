package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;

@FunctionalInterface
public interface TemplateWriteFilter<K, V extends Accessor, T extends Template<K, V>> {

	boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext, @NonNull TemplateContext<K, V, T> targetContext,
			@NonNull TemplateWriter<K, V, T> writer);

}
