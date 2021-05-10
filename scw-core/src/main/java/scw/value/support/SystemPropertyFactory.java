package scw.value.support;

import java.util.Iterator;

import scw.convert.ConversionService;
import scw.core.Ordered;
import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;
import scw.util.EnumerationConvert;
import scw.util.MultiIterator;
import scw.value.AnyValue;
import scw.value.PropertyFactory;
import scw.value.StringValue;
import scw.value.Value;

public class SystemPropertyFactory implements PropertyFactory, Ordered{
	private final ConversionService conversionService;
	
	public SystemPropertyFactory(){
		this(null);
	}
	
	public SystemPropertyFactory(@Nullable ConversionService conversionService){
		this.conversionService = conversionService;
	}
	
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}
	
	public Value getValue(String key) {
		String value = System.getProperty(key);
		if(value == null){
			value = System.getenv(key);
		}

		if(value == null){
			return null;
		}
		
		if(conversionService == null){
			return new StringValue(value);
		}else{
			return new AnyValue(value, conversionService);
		}
	}

	public Iterator<String> iterator() {
		return new MultiIterator<String>(CollectionUtils
						.toIterator(EnumerationConvert.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator());
	}
}
