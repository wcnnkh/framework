package scw.value.property;

import java.util.Enumeration;

import scw.util.MultiEnumeration;
import scw.value.StringValue;
import scw.value.Value;

public abstract class StringValuePropertyFactory extends PropertyFactory {

	public StringValuePropertyFactory() {
		super(true, true);
	}
	
	@Override
	public final Value get(String key) {
		String value = getStringValue(key);
		if (value != null) {
			return new StringValue(value, getDefaultValue(key));
		}
		return super.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Enumeration<String> enumerationKeys() {
		Enumeration<String> enumeration = internalEnumerationKeys();
		if (enumeration == null) {
			return super.enumerationKeys();
		}
		return new MultiEnumeration<String>(enumeration,
				super.enumerationKeys());
	}

	protected abstract String getStringValue(String key);

	protected abstract Enumeration<String> internalEnumerationKeys();
}
