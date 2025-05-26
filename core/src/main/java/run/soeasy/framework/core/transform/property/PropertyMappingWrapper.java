package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.TemplateMappingWrapper;

public interface PropertyMappingWrapper<W extends PropertyMapping> extends PropertyMapping,
		TemplateMappingWrapper<PropertyAccessor, W>, PropertyTemplateWrapper<PropertyAccessor, W> {
	@Override
	default PropertyMapping asMap() {
		return getSource().asMap();
	}

	@Override
	default PropertyMapping asArray() {
		return getSource().asArray();
	}

	@Override
	default PropertyAccessor get(Object key) throws NoUniqueElementException {
		return getSource().get(key);
	}
}
