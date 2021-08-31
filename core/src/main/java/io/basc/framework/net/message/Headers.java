package io.basc.framework.net.message;

import io.basc.framework.util.AbstractMultiValueMap;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.LinkedCaseInsensitiveMap;
import io.basc.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Headers extends AbstractMultiValueMap<String, String> {
	private static final long serialVersionUID = 1L;
	public static final Headers EMPTY = new Headers(Collections.emptyMap(), false);
	
	private Map<String, List<String>> headers;
	private boolean readyOnly;

	public Headers(boolean caseSensitiveKey) {
		if (caseSensitiveKey) {
			this.headers = new LinkedHashMap<String, List<String>>(8);
		} else {
			this.headers = new LinkedCaseInsensitiveMap<List<String>>(8,
					Locale.ENGLISH);
		}
	}

	public Headers(Map<String, List<String>> headers,
			boolean caseSensitiveKey) {
		this(caseSensitiveKey);
		if(!CollectionUtils.isEmpty(headers)){
			putAll(headers);
		}
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
				map = new LinkedCaseInsensitiveMap<List<String>>(
						headers.size(), Locale.ENGLISH);
				map.putAll(headers);
			}
		}
		this.headers = map;
	}
	
	public final boolean isReadyOnly() {
		return readyOnly;
	}
	
	public final boolean isCaseSensitiveKey(){
		return !(headers instanceof LinkedCaseInsensitiveMap);
	}

	public void readyOnly() {
		if (isReadyOnly()) {
			return;
		}
		this.readyOnly = true;
		this.headers = Collections.unmodifiableMap(this.headers);
	}

	public void readyOnly(boolean caseSensitiveKey) {
		caseSensitiveKey(caseSensitiveKey);
		readyOnly();
	}

	@Override
	protected final Map<String, List<String>> getTargetMap() {
		return headers;
	}

	/**
	 * Return all values of a given header name, even if this header is set
	 * multiple times.
	 */
	public List<String> getValuesAsList(String headerName, String tokenize) {
		List<String> values = get(headerName);
		if (values != null) {
			List<String> result = new ArrayList<String>();
			for (String value : values) {
				if (value != null) {
					String[] tokens = StringUtils.tokenizeToStringArray(value,
							tokenize);
					for (String token : tokens) {
						result.add(token);
					}
				}
			}
			return result;
		}
		return Collections.emptyList();
	}
}