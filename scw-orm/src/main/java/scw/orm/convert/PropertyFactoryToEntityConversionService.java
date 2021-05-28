package scw.orm.convert;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import scw.convert.lang.ConvertiblePair;
import scw.core.utils.CollectionUtils;
import scw.value.PropertyFactory;

public class PropertyFactoryToEntityConversionService extends EntityConversionService{
	
	@Override
	protected Enumeration<String> keys(Object source) {
		return CollectionUtils.toEnumeration(((PropertyFactory)source).iterator());
	}

	@Override
	protected Object getProperty(Object source, String key) {
		return ((PropertyFactory)source).getValue(key);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(PropertyFactory.class, Object.class));
	}
}
