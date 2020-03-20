package scw.core;

import java.util.LinkedList;

public class MultiPropertyFactory extends LinkedList<PropertyFactory> implements PropertyFactory {
	private static final long serialVersionUID = 1L;

	public String getProperty(String key) {
		for (PropertyFactory propertyFactory : this) {
			String value = propertyFactory.getProperty(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
}
