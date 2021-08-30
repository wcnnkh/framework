package io.basc.framework.value.support;

import io.basc.framework.convert.ConvertibleIterator;
import io.basc.framework.convert.lang.ObjectToStringConverter;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.PropertyFactory;

import java.util.Iterator;
import java.util.Properties;

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
