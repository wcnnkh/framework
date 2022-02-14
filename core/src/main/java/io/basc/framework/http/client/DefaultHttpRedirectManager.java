package io.basc.framework.http.client;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.HttpStatus;

public class DefaultHttpRedirectManager implements RedirectManager {
	private final int maxDeep;

	public DefaultHttpRedirectManager(int maxDeep) {
		this.maxDeep = maxDeep;
	}

	public URI getRedirect(HttpRequest request, ClientHttpResponse response, long deep) throws IOException {
		return getLocation(response.getStatusCode(), response.getHeaders(), deep);
	}

	public URI getRedirect(HttpRequest request, HttpResponseEntity<?> responseEntity, long deep) {
		return getLocation(responseEntity.getStatusCode(), responseEntity.getHeaders(), deep);
	}

	public URI getLocation(HttpStatus statusCode, HttpHeaders httpHeaders, long deep) {
		if (maxDeep > deep) {
			return null;
		}

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