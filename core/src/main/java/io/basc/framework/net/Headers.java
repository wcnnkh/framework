package io.basc.framework.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.AbstractMultiValueMap;
import io.basc.framework.util.collect.LinkedCaseInsensitiveMap;

public class Headers extends AbstractMultiValueMap<String, String> {
	private static final long serialVersionUID = 1L;
	public static final Headers EMPTY = new Headers(Collections.emptyMap(), true);

	private Map<String, List<String>> headers;
	private boolean readyOnly;

	public Headers(boolean caseSensitiveKey) {
		if (caseSensitiveKey) {
			this.headers = new LinkedHashMap<String, List<String>>(8);
		} else {
			this.headers = new LinkedCaseInsensitiveMap<List<String>>(8, Locale.ENGLISH);
		}
	}

	public Headers(Map<String, List<String>> headers, boolean readyOnly) {
		this.headers = readyOnly ? Collections.unmodifiableMap(headers) : headers;
		this.readyOnly = readyOnly;
	}

	/**
	 * 克隆一个新的
	 * 
	 * @param headers
	 */
	public Headers(Headers headers) {
		this.headers = headers.readyOnly ? headers.headers : CollectionFactory.clone(headers.headers);
		this.readyOnly = headers.readyOnly;
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

	public final boolean isReadyOnly() {
		return readyOnly;
	}

	public final boolean isCaseSensitiveKey() {
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
	 * Return all values of a given header name, even if this header is set multiple
	 * times.
	 */
	public List<String> getValuesAsList(String headerName, String tokenize) {
		List<String> values = get(headerName);
		if (values != null) {
			List<String> result = new ArrayList<String>();
			for (String value : values) {
				if (value != null) {
					String[] tokens = StringUtils.tokenizeToArray(value, tokenize);
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
