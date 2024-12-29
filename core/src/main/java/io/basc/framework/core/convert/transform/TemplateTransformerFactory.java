package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;

public interface TemplateTransformerFactory<K, SV extends Value, S extends Template<K, SV>, TV extends Accessor, T extends Template<K, TV>, E extends Throwable> {
	TemplateTransformer<K, SV, S, TV, T, E> getTemplateTransformer(TypeDescriptor requiredTypeDescriptor);
}
