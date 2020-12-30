package scw.configure.support;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.support.ConvertiblePair;
import scw.core.utils.CollectionUtils;
import scw.value.property.BasePropertyFactory;

public class PropertyFactoryConfigure extends EntityConfigure{
	
	public PropertyFactoryConfigure(ConversionService conversionService) {
		super(conversionService);
	}

	@Override
	protected Enumeration<String> keys(Object source) {
		return CollectionUtils.toEnumeration(((BasePropertyFactory)source).iterator());
	}

	@Override
	protected Object getProperty(Object source, String key) {
		return ((BasePropertyFactory)source).getValue(key);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(BasePropertyFactory.class, Object.class));
	}
}
