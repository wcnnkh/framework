package scw.value.support;

import java.util.Iterator;
import java.util.Properties;

import scw.convert.ConvertibleIterator;
import scw.convert.lang.ObjectToStringConverter;
import scw.core.utils.CollectionUtils;
import scw.value.PropertyFactory;

public class PropertiesPropertyFactory extends PropertiesValueFactory<String> implements PropertyFactory{

	public PropertiesPropertyFactory(Properties properties) {
		super(properties);
	}

	public Iterator<String> iterator() {
		return new ConvertibleIterator<Object, String>(CollectionUtils.toIterator(properties.keys()), new ObjectToStringConverter());
	}

	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

}
