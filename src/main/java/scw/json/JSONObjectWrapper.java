package scw.json;

public class JSONObjectWrapper extends JSONObjectReadOnlyWarpper implements JSONObject {
	private static final long serialVersionUID = 1L;
	private JSONObject jsonObject;

	public JSONObjectWrapper(JSONObject jsonObject) {
		super(jsonObject);
		this.jsonObject = jsonObject;
	}

	public void put(String key, Object value) {
		jsonObject.put(key, value);
	}

	public void remove(String key) {
		jsonObject.remove(key);
	}

	@Override
	public String toString() {
		return jsonObject.toJSONString();
	}
}
