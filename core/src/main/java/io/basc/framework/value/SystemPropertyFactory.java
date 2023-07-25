package io.basc.framework.value;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.ConvertibleEnumeration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.MultiIterator;

public class SystemPropertyFactory implements PropertyFactory {
	public static final SystemPropertyFactory INSTANCE = new SystemPropertyFactory();

	public Value get(String key) {
		String value = System.getProperty(key);
		if (value == null) {
			value = System.getenv(key);
		}

		return Value.of(value);
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> new MultiIterator<String>(
				CollectionUtils
						.toIterator(ConvertibleEnumeration.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator()));
	}
}
