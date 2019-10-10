package scw.core.header;

import java.util.List;
import java.util.Map;

import scw.core.multivalue.MultiValueMap;
import scw.core.multivalue.MultiValueMapWrapper;

public class SimpleMultiValueHeadersReadOnly extends AbstractMultiValueHeaderReadOnly {
	private final MultiValueMap<String, String> headerMap;

	public SimpleMultiValueHeadersReadOnly(MultiValueMap<String, String> headerMap) {
		this.headerMap = headerMap;
	}

	public SimpleMultiValueHeadersReadOnly(Map<String, List<String>> headerMap) {
		this.headerMap = new MultiValueMapWrapper<String, String>(headerMap);
	}

	public final MultiValueMap<String, String> getHeaderMap() {
		return headerMap;
	}
}
