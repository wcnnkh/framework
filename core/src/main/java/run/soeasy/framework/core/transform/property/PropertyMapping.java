package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.templates.TemplateMapping;

@FunctionalInterface
public interface PropertyMapping<V extends PropertyAccessor> extends PropertyTemplate<V>, TemplateMapping<V> {

	@Override
	default V get(Object key) throws NoUniqueElementException {
		return PropertyTemplate.super.get(key);
	}

	@Override
	default PropertyMapping<V> asMap(boolean uniqueness) {
		return new MapPropertyMapping<>(this, uniqueness);
	}

	@Override
	default PropertyMapping<V> asArray(boolean uniqueness) {
		return new ArrayPropertyMapping<>(this, uniqueness);
	}
}
