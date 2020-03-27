package scw.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import scw.core.StringFormat;
import scw.util.FormatUtils;
import scw.util.value.property.PropertyFactory;

public abstract class JsonObject extends AbstractJson<String> implements PropertyFactory {

	public abstract void put(String key, Object value);
	
	public Enumeration<String> enumerationKeys() {
		return Collections.enumeration(keys());
	}

	public abstract Collection<String> keys();

	public abstract boolean containsKey(String key);
	
	public String format(String text, boolean supportEL) {
		return FormatUtils.format(text, this, supportEL);
	}

	public String format(String text, String prefix, String suffix) {
		return StringFormat.format(text, prefix, suffix, this);
	}
}
