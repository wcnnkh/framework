package scw.json;

public class JSONObjectWrapper extends JSONObjectReadOnlyWarpper implements JSONObject {
	private static final long serialVersionUID = 1L;
	private JSONObject jsonObject;

	public JSONObjectWrapper(JSONObject jsonObject) {
		super(jsonObject);
		this.jsonObject = jsonObject;
	}

	public JSONObject put(String key, Object value) {
		return jsonObject.put(key, value);
	}

	public Object remove(String key) {
		return jsonObject.remove(key);
	}
	
	@Override
	public String toString() {
		return jsonObject.toJSONString();
	}
}
