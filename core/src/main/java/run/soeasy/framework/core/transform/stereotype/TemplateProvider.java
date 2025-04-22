package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

public interface TemplateProvider<S, K, V extends ValueAccessor, T extends Template<K, V>>
		extends TemplateFactory<S, K, V, T> {
	boolean hasTemplate(@NonNull TypeDescriptor requiredType);
}
