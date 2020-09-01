package scw.json;

import scw.value.Value;

public interface JsonElement extends Value {
	JsonArray getAsJsonArray();

	JsonObject getAsJsonObject();
	
	boolean isJsonArray();
	
	boolean isJsonObject();
	
	String toJsonString();
}
