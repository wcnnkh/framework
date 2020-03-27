package scw.json;

import scw.util.value.AbstractValue;
import scw.util.value.Value;

public abstract class AbstractJsonElement extends AbstractValue implements JsonElement {

	public AbstractJsonElement(Value defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return getAsString();
	}
}
