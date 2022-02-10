package io.basc.framework.http.client;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.HttpStatus;

public class DefaultHttpRedirectManager implements RedirectManager {

	public URI getRedirect(ClientHttpResponse response, long deep) throws IOException {
		return getLocation(response.getStatusCode(), response.getHeaders());
	}

	public URI getRedirect(HttpResponseEntity<?> responseEntity, long deep) {
		return getLocation(responseEntity.getStatusCode(), responseEntity.getHeaders());
	}

	public URI getLocation(HttpStatus statusCode, HttpHeaders httpHeaders) {
		// 重定向
		if (statusCode == HttpStatus.MOVED_PERMANENTLY || statusCode == HttpStatus.FOUND) {
			URI location = httpHeaders.getLocation();
			if (location != null) {
				return location;
			}
			return location;
		}
		return null;
	}
}