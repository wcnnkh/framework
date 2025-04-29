package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;

@FunctionalInterface
public interface TemplateWriter<K, V extends TypedValueAccessor, T extends Template<K, V>> {

	boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext, @NonNull TemplateContext<K, V, T> targetContext);
}