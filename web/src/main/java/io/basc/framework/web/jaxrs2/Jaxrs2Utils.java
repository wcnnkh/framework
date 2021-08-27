package io.basc.framework.web.jaxrs2;

import io.basc.framework.http.HttpHeaders;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class Jaxrs2Utils {
	private Jaxrs2Utils() {
	}

	public static MediaType convertMediaType(io.basc.framework.http.MediaType mediaType) {
		if(mediaType == null) {
			return null;
		}
		return new MediaType(mediaType.getType(), mediaType.getSubtype(), mediaType.getParameters());
	}

	public static MultivaluedMap<String, String> convertHeaders(HttpHeaders headers) {
		MultivaluedMap<String, String> headerMap = new MultivaluedHashMap<>();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			headerMap.put(entry.getKey(), entry.getValue());
		}
		return headerMap;
	}
}
