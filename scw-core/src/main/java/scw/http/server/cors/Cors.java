package scw.http.server.cors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;

public class Cors {
	private static final String[] DEFAULT_HEADERS = new String[] { HttpHeaders.X_REQUESTED_WITH,
			HttpHeaders.CONTENT_TYPE, HttpHeaders.X_FORWARDED_FOR, HttpHeaders.COOKIE,
			HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpHeaders.ACCEPT,
			HttpHeaders.ORIGIN };
	public static final Cors DEFAULT = new Cors(Arrays.asList(DEFAULT_HEADERS), null, null, false, -1);

	/**
	 * 允许跨域的请求头
	 */
	private List<String> headers;
	/**
	 * 允许跨域的方法
	 */
	private List<String> methods;
	/**
	 * 允许跨域的主机地址
	 */
	private List<String> origins;
	/**
	 * 是否携带cookie
	 */
	private boolean credentials = true;
	/**
	 * 重新预检验跨域的缓存时间 (s)
	 */
	private int maxAge = -1;

	public Cors() {
	};

	public Cors(List<String> headers, List<String> methods, List<String> origins, boolean credentials, int maxAge) {
		this.headers = headers;
		this.methods = methods;
		this.origins = origins;
		this.credentials = credentials;
		this.maxAge = maxAge;
	}

	public List<String> getHeaders() {
		if (headers == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(headers);
	}

	public Cors setHeaders(List<String> headers) {
		this.headers = headers;
		return this;
	}

	public List<String> getMethods() {
		if (methods == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(methods);
	}

	public Cors setMethods(List<String> methods) {
		this.methods = methods;
		return this;
	}

	public List<String> getOrigins() {
		if (origins == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(origins);
	}

	public Cors setOrigins(List<String> origins) {
		this.origins = origins;
		return this;
	}

	public boolean isCredentials() {
		return credentials;
	}

	public Cors setCredentials(boolean credentials) {
		this.credentials = credentials;
		return this;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public Cors setMaxAge(int maxAge) {
		this.maxAge = maxAge;
		return this;
	}

	private void write(HttpHeaders headers, String headerName, Collection<String> values, String defaultValue) {
		if (CollectionUtils.isEmpty(values)) {
			headers.set(headerName, defaultValue);
		} else {
			int size = 0;
			for (String header : values) {
				if (!StringUtils.hasText(header)) {
					continue;
				}
				headers.add(headerName, header);
				size++;
			}

			if (size == 0) {
				headers.set(headerName, defaultValue);
			}
		}
	}

	public void write(HttpHeaders headers) {
		write(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, this.origins, "*");
		write(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, this.methods, "*");
		write(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, this.headers, "*");
		if (maxAge > 0) {
			headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, maxAge + "");
		}
		headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, credentials + "");
	}
}
