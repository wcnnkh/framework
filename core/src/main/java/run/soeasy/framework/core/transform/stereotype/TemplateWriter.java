package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.ValueAccessor;

@FunctionalInterface
public interface TemplateWriter<K, V extends ValueAccessor, T extends Template<K, V>> {

	boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext, @NonNull TemplateContext<K, V, T> targetContext);
}