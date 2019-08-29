package scw.json;

import java.util.Iterator;

public class JSONArrayWrapper extends JSONArrayReadOnlyWrapper implements JSONArray {
	private static final long serialVersionUID = 1L;
	private JSONArray jsonArray;

	public JSONArrayWrapper(JSONArray jsonArray) {
		super(jsonArray);
		this.jsonArray = jsonArray;
	}

	public Iterator<?> iterator() {
		return jsonArray.iterator();
	}

	public void add(Object e) {
		jsonArray.add(e);
	}

	public void remove(int index) {
		jsonArray.remove(index);
	}

	@Override
	public String toString() {
		return jsonArray.toString();
	}
}
