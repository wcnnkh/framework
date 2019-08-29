package scw.json;

import java.util.Iterator;

public class JSONArrayWrapper extends JSONArrayReadOnlyWrapper implements JSONArray {
	private static final long serialVersionUID = 1L;
	private JSONArray jsonArray;

	public JSONArrayWrapper(JSONArray jsonArray) {
		super(jsonArray);
		this.jsonArray = jsonArray;
	}

	public Iterator<Object> iterator() {
		return jsonArray.iterator();
	}

	public JSONArray add(Object e) {
		jsonArray.add(e);
		return this;
	}

	public JSONArray add(int index, Object element) {
		jsonArray.add(index, element);
		return this;
	}

	public Object remove(int index) {
		return jsonArray.remove(index);
	}

	@Override
	public String toString() {
		return jsonArray.toString();
	}
}
