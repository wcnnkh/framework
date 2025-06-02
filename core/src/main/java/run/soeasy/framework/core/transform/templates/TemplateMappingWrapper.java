package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.LookupWrapper;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

public interface TemplateMappingWrapper<E extends TypedValueAccessor, W extends TemplateMapping<E>>
		extends TemplateMapping<E>, MappingWrapper<Object, E, W>, LookupWrapper<Object, E, W>, TemplateWrapper<E, W> {

	@Override
	default E get(Object key) {
		return getSource().get(key);
	}

	@Override
	default TemplateMapping<E> asMap(boolean uniqueness) {
		return getSource().asMap(uniqueness);
	}

	@Override
	default TemplateMapping<E> asArray(boolean uniqueness) {
		return getSource().asArray(uniqueness);
	}
}