package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.TemplateMapping;

@FunctionalInterface
public interface PropertyMapping extends PropertyTemplate<PropertyAccessor>, TemplateMapping<PropertyAccessor> {
	@Override
	default PropertyMapping asMap() {
		return new MapPropertyMapping<>(this);
	}

	@Override
	default PropertyMapping asArray() {
		return this;
	}

	@Override
	default PropertyAccessor get(Object key) throws NoUniqueElementException {
		return PropertyTemplate.super.get(key);
	}
}
