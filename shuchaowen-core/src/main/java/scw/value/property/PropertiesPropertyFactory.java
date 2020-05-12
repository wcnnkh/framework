package scw.value.property;

import java.util.Enumeration;
import java.util.Properties;

import scw.core.Converter;
import scw.util.EnumerationConvert;

public class PropertiesPropertyFactory extends StringValuePropertyFactory {
	private Properties properties;

	public PropertiesPropertyFactory(Properties properties) {
		this.properties = properties;
	}

	@Override
	protected Enumeration<String> internalEnumerationKeys() {
		return new EnumerationConvert<Object, String>(properties.keys(),
				new Converter<Object, String>() {

					public String convert(Object k) throws Exception {
						return k.toString();
					}
				});
	}

	@Override
	protected String getStringValue(String key) {
		if (properties.contains(key)) {
			return properties.getProperty(key);
		}
		return null;
	}

}
