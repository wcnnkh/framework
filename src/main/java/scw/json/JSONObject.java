package scw.json;

public interface JSONObject extends JSONObjectReadOnly {
	void put(String key, Object value);

	void remove(String key);
}
