package io.basc.framework.value;

import java.util.Properties;

import io.basc.framework.convert.lang.ObjectToString;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConvertibleIterator;
import io.basc.framework.util.Elements;

public class PropertiesPropertyFactory extends PropertiesValueFactory<String> implements PropertyFactory {

	public PropertiesPropertyFactory(Properties properties) {
		super(properties);
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> new ConvertibleIterator<Object, String>(CollectionUtils.toIterator(properties.keys()),
				ObjectToString.DEFAULT));
	}

	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

}
