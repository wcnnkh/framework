package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

public class MapTemplateMapping<E extends TypedValueAccessor, W extends TemplateMapping<E>> extends MapTemplate<E, W>
		implements TemplateMappingWrapper<E, W> {

	public MapTemplateMapping(@NonNull W source) {
		super(source);
	}

	@Override
	public TemplateMapping<E> asMap() {
		return this;
	}

	@Override
	public TemplateMapping<E> asArray() {
		return getSource();
	}
}
