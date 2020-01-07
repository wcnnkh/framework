package scw.net.header;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import scw.util.MultiValueMap;

public abstract class AbstractMultiValueHeaderReadOnly implements MultiValueHeadersReadOnly {
	protected abstract MultiValueMap<String, String> getHeaderMap();

	public String getHeader(String name) {
		MultiValueMap<String, String> headerMap = getHeaderMap();
		return headerMap == null ? null : headerMap.getFirst(name);
	}

	public Enumeration<String> getHeaders(String name) {
		MultiValueMap<String, String> headerMap = getHeaderMap();
		if (headerMap == null) {
			return Collections.emptyEnumeration();
		}

		List<String> values = headerMap.get(name);
		if (values == null) {
			return Collections.emptyEnumeration();
		}

		return Collections.enumeration(values);
	}

	public Enumeration<String> getHeaderNames() {
		MultiValueMap<String, String> headerMap = getHeaderMap();
		if (headerMap == null) {
			return Collections.emptyEnumeration();
		}

		return Collections.enumeration(headerMap.keySet());
	}
}
