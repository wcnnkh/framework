package scw.servlet.view.common;

import java.util.HashMap;
import java.util.Map;

public class MapResult extends DataResult<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	public MapResult() {
		setData(new HashMap<String, Object>(8, 1));
	}

	public MapResult put(String key, Object value) {
		getData().put(key, value);
		return this;
	}
}
