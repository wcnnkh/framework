package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.Lookup;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.transform.Mapping;

@FunctionalInterface
public interface TemplateMapping<E extends TypedValueAccessor>
		extends Mapping<Object, E>, Lookup<Object, E>, Template<E> {

	@Override
	default TemplateMapping<E> asArray() {
		return this;
	}

	@Override
	default TemplateMapping<E> asMap() {
		return new MapTemplateMapping<>(this);
	}

	@Override
	default E get(Object key) throws NoUniqueElementException {
		return Template.super.get(key);
	}
}
