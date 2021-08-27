package io.basc.framework.orm.convert;

import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.value.PropertyFactory;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

public class PropertyFactoryToEntityConversionService extends EntityConversionService{
	
	@Override
	protected Enumeration<String> keys(Object source) {
		return CollectionUtils.toEnumeration(((PropertyFactory)source).iterator());
	}

	@Override
	protected Object getProperty(Object source, String key) {
		return ((PropertyFactory)source).getValue(key);
	}
	
	@Override
	protected boolean containsKey(Object source, String key) {
		return ((PropertyFactory)source).containsKey(key);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(PropertyFactory.class, Object.class));
	}
}
