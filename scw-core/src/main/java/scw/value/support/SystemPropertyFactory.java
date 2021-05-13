package scw.value.support;

import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.util.EnumerationConvert;
import scw.util.MultiIterator;
import scw.value.PropertyFactory;
import scw.value.StringValue;
import scw.value.Value;

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
						.toIterator(EnumerationConvert.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator());
	}
}
