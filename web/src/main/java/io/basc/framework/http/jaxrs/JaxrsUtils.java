package io.basc.framework.http.jaxrs;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import io.basc.framework.net.Headers;
import io.basc.framework.util.io.MimeType;

public class JaxrsUtils {
	private JaxrsUtils() {
	}

	public static MediaType convertMediaType(MimeType contentType) {
		if(contentType == null) {
			return null;
		}
		return new MediaType(contentType.getType(), contentType.getSubtype(), contentType.getParameters());
	}

	public static MultivaluedMap<String, String> convertHeaders(Headers headers) {
		MultivaluedMap<String, String> headerMap = new MultivaluedHashMap<>();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			headerMap.put(entry.getKey(), entry.getValue());
		}
		return headerMap;
	}
}
