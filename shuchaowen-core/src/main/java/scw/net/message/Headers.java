package scw.net.message;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import scw.util.AbstractMultiValueMap;
import scw.util.LinkedCaseInsensitiveMap;

public class Headers extends AbstractMultiValueMap<String, String> {
	private static final long serialVersionUID = 1L;
	protected Map<String, List<String>> headers;

	public Headers(boolean caseSensitiveKey) {
		if (caseSensitiveKey) {
			this.headers = new LinkedHashMap<String, List<String>>(8);
		} else {
			this.headers = new LinkedCaseInsensitiveMap<List<String>>(8, Locale.ENGLISH);
		}
	}

	public Headers(Map<String, List<String>> wrapperHeaders, boolean caseSensitiveKey) {
		this.headers = wrapperHeaders;
		caseSensitiveKey(caseSensitiveKey);
	}

	public void caseSensitiveKey(boolean caseSensitiveKey) {
		Map<String, List<String>> map = headers;
		if (caseSensitiveKey) {
			if (headers instanceof LinkedCaseInsensitiveMap) {
				map = new LinkedHashMap<String, List<String>>();
				map.putAll(headers);
			}
		} else {
			if (!(headers instanceof LinkedCaseInsensitiveMap)) {
				map = new LinkedCaseInsensitiveMap<List<String>>(headers.size(), Locale.ENGLISH);
				map.putAll(headers);
			}
		}
		this.headers = map;
	}

	public void readyOnly() {
		this.headers = Collections.unmodifiableMap(this.headers);
	}

	public void readyOnly(boolean caseSensitiveKey) {
		caseSensitiveKey(caseSensitiveKey);
		readyOnly();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, List<String>> getTargetMap() {
		return (Map<String, List<String>>) (headers == null ? Collections.emptyMap() : headers);
	}

	protected void setTargetMap(Map<String, List<String>> targetMap) {
		this.headers = targetMap;
	}
}
