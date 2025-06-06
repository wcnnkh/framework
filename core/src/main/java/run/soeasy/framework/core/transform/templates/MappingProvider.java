package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

public interface MappingProvider<S, K, V extends TypedValueAccessor, T extends Mapping<K, V>>
		extends MappingFactory<S, K, V, T>{
	boolean hasMapping(@NonNull TypeDescriptor requiredType);
}
