package scw.util.value.property;

import java.util.Enumeration;
import java.util.Properties;

import scw.core.Converter;
import scw.util.EnumerationConvert;
import scw.util.value.StringValue;
import scw.util.value.Value;

public class PropertiesPropertyFactory extends AbstractPropertyFactory{
	private Properties properties;
	
	public PropertiesPropertyFactory(Properties properties){
		this.properties = properties;
	}
	
	public Value get(String key) {
		if(properties.contains(key)){
			return new StringValue(properties.getProperty(key));
		}
		return null;
	}

	public Enumeration<String> enumerationKeys() {
		return new EnumerationConvert<Object, String>(properties.keys(), new Converter<Object, String>() {

			public String convert(Object k) throws Exception {
				return k.toString();
			}
		});
	}

}
