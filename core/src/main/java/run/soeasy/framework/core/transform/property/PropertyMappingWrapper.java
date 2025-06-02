package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.templates.TemplateMappingWrapper;

public interface PropertyMappingWrapper<V extends PropertyAccessor, W extends PropertyMapping<V>>
		extends PropertyMapping<V>, TemplateMappingWrapper<V, W>, PropertyTemplateWrapper<V, W> {
	@Override
	default PropertyMapping<V> asMap() {
		return getSource().asMap();
	}

	@Override
	default PropertyMapping<V> asArray() {
		return getSource().asArray();
	}

	@Override
	default V get(Object key) throws NoUniqueElementException {
		return getSource().get(key);
	}
}
