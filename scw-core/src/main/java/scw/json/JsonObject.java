package scw.json;

import java.util.Set;

import scw.util.Pair;

public interface JsonObject extends Json<String>, Iterable<Pair<String, JsonElement>>{
	static final String PREFIX = "{";
	static final String SUFFIX = "}";
	
	Set<String> keySet();

	boolean remove(String key);
	
	boolean put(String key, Object value);
}