package io.basc.framework.value.support;

import io.basc.framework.convert.ConvertibleEnumeration;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

import java.util.Iterator;

public class SystemPropertyFactory implements PropertyFactory {
	public static final SystemPropertyFactory INSTANCE = new SystemPropertyFactory();

	public Value getValue(String key) {
		String value = System.getProperty(key);
		if (value == null) {
			value = System.getenv(key);
		}

		if (value == null) {
			return null;
		}

		return new StringValue(value);
	}

	public Iterator<String> iterator() {
		return new MultiIterator<String>(
				CollectionUtils
						.toIterator(ConvertibleEnumeration.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator());
	}
}
