package scw.convert.support;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import scw.convert.ConversionService;
import scw.core.utils.CollectionUtils;
import scw.util.EnumerationConvert;

@SuppressWarnings("rawtypes")
public class MapToEntityConversionService extends EntityConversionService{

	public MapToEntityConversionService(ConversionService conversionService) {
		super(conversionService);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Enumeration<String> keys(Object source) {
		return EnumerationConvert.convertToStringEnumeration(CollectionUtils.toEnumeration(((Map)source).keySet().iterator()));
	}
	
	@Override
	protected Object getProperty(Object source, String key) {
		return ((Map)source).get(key);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Map.class, Object.class));
	}

}
