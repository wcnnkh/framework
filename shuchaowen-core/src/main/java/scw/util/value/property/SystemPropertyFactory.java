package scw.util.value.property;

import java.util.Collections;
import java.util.Enumeration;

import scw.util.EnumerationConvert;
import scw.util.MultiEnumeration;

public final class SystemPropertyFactory extends StringValuePropertyFactory implements PropertyFactory{
	private static SystemPropertyFactory instance = new SystemPropertyFactory();

	private SystemPropertyFactory() {
	};

	public static SystemPropertyFactory getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> enumerationKeys() {
		return EnumerationConvert
				.convertToStringEnumeration(new MultiEnumeration<Object>(System
						.getProperties().keys(), Collections.enumeration(System
						.getenv().keySet())));
	}
	
	@Override
	protected String getValue(String key) {
		String v = System.getProperty(key);
		if (v == null) {
			v = System.getenv(key);
		}
		return v;
	}
}
