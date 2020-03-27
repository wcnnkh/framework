package scw.json;

import scw.util.value.DefaultValueDefinition;

public class DefaultJsonElementValue extends DefaultValueDefinition implements JsonElement {
	private static final long serialVersionUID = 1L;
	public static final DefaultJsonElementValue DEFAULT_JSON_ELEMENT_VALUE = new DefaultJsonElementValue();

	public JsonArray getAsJsonArray() {
		return null;
	}

	public JsonObject getAsJsonObject() {
		return null;
	}
}
