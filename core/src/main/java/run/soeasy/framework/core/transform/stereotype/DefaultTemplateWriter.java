package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.ValueAccessor;

public class DefaultTemplateWriter<K, V extends ValueAccessor, T extends Template<K, V>>
		extends FilterableTemplateWriter<K, V, T> implements TemplateWriter<K, V, T> {

	public DefaultTemplateWriter() {
		super(new TemplateWriteFilters<>(), new GenericTemplateWriter<>());
	}

	@Override
	public @NonNull TemplateWriteFilters<K, V, T> getFilters() {
		return (TemplateWriteFilters<K, V, T>) super.getFilters();
	}

	@Override
	public @NonNull GenericTemplateWriter<K, V, T> getTemplateWriter() {
		return (run.soeasy.framework.core.transform.stereotype.GenericTemplateWriter<K, V, T>) super.getTemplateWriter();
	}
}
