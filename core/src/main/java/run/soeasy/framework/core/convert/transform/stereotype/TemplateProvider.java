package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface TemplateProvider<S, K, V extends Source, T extends Template<K, ? extends V>>
		extends TemplateFactory<S, K, V, T> {
	boolean hasTemplate(@NonNull TypeDescriptor requiredType);
}
