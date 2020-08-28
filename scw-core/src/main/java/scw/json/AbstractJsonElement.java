package scw.json;

import scw.value.AbstractStringValue;
import scw.value.Value;

public abstract class AbstractJsonElement extends AbstractStringValue implements JsonElement {

	public AbstractJsonElement(Value defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return getAsString();
	}
}
