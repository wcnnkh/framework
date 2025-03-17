package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface TemplateFactory<S, K, V extends Source, T extends Template<K, ? extends V>> {
	T getTemplate(@NonNull S source, @NonNull TypeDescriptor requiredType);
}
