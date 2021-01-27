package scw.convert.support;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import scw.convert.ConversionService;
import scw.core.utils.CollectionUtils;
import scw.value.factory.PropertyFactory;

public class PropertyFactoryToEntityConversionService extends EntityConversionService{
	
	public PropertyFactoryToEntityConversionService(ConversionService conversionService) {
		super(conversionService);
		setStrict(false);//默认非严格模式
	}
	
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
