package scw.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import scw.value.property.BasePropertyFactory;

public abstract class JsonObject extends AbstractJson<String> implements BasePropertyFactory {

	public abstract void put(String key, Object value);
	
	
	public Enumeration<String> enumerationKeys() {
		return Collections.enumeration(keys());
	}

	public abstract Collection<String> keys();

	public abstract boolean containsKey(String key);
}
