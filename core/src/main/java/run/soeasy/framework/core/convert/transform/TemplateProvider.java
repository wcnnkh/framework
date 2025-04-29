package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValueAccessor;

public interface TemplateProvider<S, K, V extends TypedValueAccessor, T extends Template<K, V>>
		extends TemplateFactory<S, K, V, T> {
	boolean hasTemplate(@NonNull TypeDescriptor requiredType);
}
