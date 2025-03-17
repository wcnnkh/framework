package run.soeasy.framework.http.client;

import java.io.IOException;
import java.net.URI;

import run.soeasy.framework.http.HttpHeaders;
import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.http.HttpResponseEntity;
import run.soeasy.framework.http.HttpStatus;

public class DefaultHttpRedirectManager implements RedirectManager {
	private final long maxDeep;

	/**
	 * 默认最多进行3次重定向
	 */
	public DefaultHttpRedirectManager() {
		this(3);
	}

	public DefaultHttpRedirectManager(long maxDeep) {
		this.maxDeep = maxDeep;
	}

	public URI getRedirect(HttpRequest request, ClientHttpResponse response, long deep) throws IOException {
		return getLocation(response.getStatusCode(), response.getHeaders(), deep);
	}

	public URI getRedirect(HttpRequest request, HttpResponseEntity<?> responseEntity, long deep) {
		return getLocation(responseEntity.getStatusCode(), responseEntity.getHeaders(), deep);
	}

	public URI getLocation(HttpStatus statusCode, HttpHeaders httpHeaders, long deep) {
		if (deep < 0) {
			return null;
		}

		if (maxDeep >= 0 && deep >= maxDeep) {
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