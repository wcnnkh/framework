package scw.core;

import java.util.Iterator;
import java.util.LinkedList;

public class MultiPropertyFactory extends LinkedList<PropertyFactory> implements PropertyFactory, Destroy {
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

	public void destroy() {
		Iterator<PropertyFactory> iterator = this.iterator();
		while (iterator.hasNext()) {
			PropertyFactory propertyFactory = iterator.next();
			if (propertyFactory instanceof Destroy) {
				((Destroy) propertyFactory).destroy();
			}
		}
	}
}
