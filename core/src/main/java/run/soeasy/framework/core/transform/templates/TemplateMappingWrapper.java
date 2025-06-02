package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.LookupWrapper;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.transform.MappingWrapper;

public interface TemplateMappingWrapper<E extends TypedValueAccessor, W extends TemplateMapping<E>>
		extends TemplateMapping<E>, MappingWrapper<Object, E, W>, LookupWrapper<Object, E, W>, TemplateWrapper<E, W> {

	@Override
	default E get(Object key) {
		return getSource().get(key);
	}

	@Override
	default TemplateMapping<E> asMap() {
		return getSource().asMap();
	}

	@Override
	default TemplateMapping<E> asArray() {
		return getSource().asArray();
	}

}