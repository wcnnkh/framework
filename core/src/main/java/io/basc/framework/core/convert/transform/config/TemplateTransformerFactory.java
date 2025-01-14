package io.basc.framework.core.convert.transform.config;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TemplateTransformer;

public interface TemplateTransformerFactory<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable> {
	TemplateTransformer<K, SV, S, TV, T, E> getTemplateTransformer(TypeDescriptor requiredTypeDescriptor);
}
