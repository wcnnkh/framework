package scw.beans.rpc;

import java.util.HashMap;

import scw.servlet.ServletUtils;

@SuppressWarnings("unchecked")
public final class HttpCalUitls {

	private static HashMap<String, Object> getSpreadData() {
		return (HashMap<String, Object>) ServletUtils.getControllerThreadLocalResource(HttpCalUitls.class);
	}	
	public static void setSpreadData(String name, Object value) {
		HashMap<String, Object> map = getSpreadData();
		if (map == null) {
			map = new HashMap<String, Object>(8);
			ServletUtils.bindControllerThreadLocalResource(HttpCalUitls.class, map);
		}
		map.put(name, value);
	}

	public static void setSpreadData(String name) {
		HashMap<String, Object> map = getSpreadData();
		if (map == null) {
			return;
		}
		map.remove(name);
	}
}
