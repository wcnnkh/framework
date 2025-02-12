package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Value;

public class DefaultTemplateWriter<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends FilterableTemplateWriter<K, SV, S, TV, T, E> {

	public DefaultTemplateWriter() {
		super(new TemplateWriteFilters<>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public TemplateWriteFilters<K, SV, S, TV, T, E> getTemplateWriteFilters() {
		return (TemplateWriteFilters<K, SV, S, TV, T, E>) super.getTemplateWriteFilters();
	}
}
