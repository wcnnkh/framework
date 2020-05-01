package scw.json;

import scw.util.value.AbstractStringValue;
import scw.util.value.Value;

public abstract class AbstractJsonElement extends AbstractStringValue implements JsonElement {

	public AbstractJsonElement(Value defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return getAsString();
	}
}
