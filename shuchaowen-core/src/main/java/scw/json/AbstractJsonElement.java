package scw.json;

import scw.util.value.AbstractValue;

public abstract class AbstractJsonElement extends AbstractValue implements JsonElement {

	@Override
	public String toString() {
		return getAsString();
	}
}
