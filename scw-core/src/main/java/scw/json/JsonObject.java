package scw.json;

import java.util.Set;

public interface JsonObject extends Json<String>{
	static final String PREFIX = "{";
	static final String SUFFIX = "}";
	
	Set<String> keySet();

	boolean containsKey(String key);
	
	boolean remove(String key);
	
	boolean put(String key, Object value);
}