package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.TemplateMapping;

@FunctionalInterface
public interface PropertyMapping<V extends PropertyAccessor> extends PropertyTemplate<V>, TemplateMapping<V> {
	@Override
	default PropertyMapping<V> asMap() {
		return new MapPropertyMapping<>(this);
	}

	@Override
	default PropertyMapping<V> asArray() {
		return this;
	}

	@Override
	default V get(Object key) throws NoUniqueElementException {
		return PropertyTemplate.super.get(key);
	}
}
