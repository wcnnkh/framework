package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

@FunctionalInterface
public interface TemplateMapping<E extends TypedValueAccessor>
		extends Mapping<Object, E>, Template<E> {

	@Override
	default TemplateMapping<E> asMap(boolean uniqueness) {
		return new ArrayTemplateMapping<>(this, uniqueness);
	}

	@Override
	default TemplateMapping<E> asArray(boolean uniqueness) {
		return new MapTemplateMapping<>(this, uniqueness);
	}

	@Override
	default E get(Object key) throws NoUniqueElementException {
		return Template.super.get(key);
	}
}
