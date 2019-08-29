package scw.json;

public interface JSONObject extends JSONObjectReadOnly {
	JSONObject put(String key, Object value);

	Object remove(String key);
}
