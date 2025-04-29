package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;

@FunctionalInterface
public interface TemplateWriteFilter<K, V extends TypedValueAccessor, T extends Template<K, V>> {

	boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext, @NonNull TemplateContext<K, V, T> targetContext,
			@NonNull TemplateWriter<K, V, T> writer);

}
