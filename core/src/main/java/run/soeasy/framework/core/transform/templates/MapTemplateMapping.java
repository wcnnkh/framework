package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.convert.value.TypedValueAccessor;

public class MapTemplateMapping<E extends TypedValueAccessor, W extends TemplateMapping<E>> extends MapTemplate<E, W>
		implements TemplateMappingWrapper<E, W> {

	public MapTemplateMapping(W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public TemplateMapping<E> asMap(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
	}
}
