package scw.mvc.rpc;

import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.lazy.LazyInitMap;

public class ShareData {
	private final Map<String, Object> parameterMap = new LazyInitMap<String, Object>() {
		protected java.util.Map<String, Object> initMap() {
			return new LinkedHashMap<String, Object>(8);
		};
	};
	private final Map<String, String> headerMap = new LazyInitMap<String, String>() {
		protected java.util.Map<String, String> initMap() {
			return new LinkedHashMap<String, String>(8);
		};
	};

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}
}
