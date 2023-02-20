package io.basc.framework.value.support;

import java.util.Iterator;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConvertibleEnumeration;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class SystemPropertyFactory implements PropertyFactory {
	public static final SystemPropertyFactory INSTANCE = new SystemPropertyFactory();

	public Value get(String key) {
		String value = System.getProperty(key);
		if (value == null) {
			value = System.getenv(key);
		}

		return Value.of(value);
	}

	public Iterator<String> iterator() {
		return new MultiIterator<String>(
				CollectionUtils
						.toIterator(ConvertibleEnumeration.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator());
	}
}
