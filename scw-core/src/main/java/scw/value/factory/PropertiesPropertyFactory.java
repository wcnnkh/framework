package scw.value.factory;

import java.util.Iterator;
import java.util.Properties;

import scw.convert.support.ObjectToStringConverter;
import scw.core.IteratorConverter;
import scw.core.utils.CollectionUtils;

public class PropertiesPropertyFactory extends PropertiesValueFactory<String> implements PropertyFactory{

	public PropertiesPropertyFactory(Properties properties) {
		super(properties);
	}

	public Iterator<String> iterator() {
		return new IteratorConverter<Object, String>(CollectionUtils.toIterator(properties.keys()), new ObjectToStringConverter());
	}

	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

}
