package scw.http.server.cors;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;
import scw.lang.NotSupportedException;

public class Cors {
	private static final String[] DEFAULT_HEADERS = new String[] {
			HttpHeaders.X_REQUESTED_WITH, HttpHeaders.CONTENT_TYPE,
			HttpHeaders.X_FORWARDED_FOR, HttpHeaders.COOKIE,
			HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
			HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpHeaders.ACCEPT,
			HttpHeaders.ORIGIN };
	public static final Cors DEFAULT = new Cors().readyOnly();
	private static final String[] EMPTY = new String[0];

	/**
	 * 允许跨域的请求头
	 */
	private String[] headers;
	/**
	 * 允许跨域的方法
	 */
	private String[] methods;
	/**
	 * 允许跨域的主机地址
	 */
	private String[] origins;
	/**
	 * 是否携带cookie
	 */
	private Boolean credentials;
	/**
	 * 重新预检验跨域的缓存时间 (s)
	 */
	private int maxAge = -1;

	private boolean readyOnly = false;

	public Cors() {
		this(DEFAULT_HEADERS, null, null, false, -1);
	};

	public Cors(String[] headers, String[] methods, String[] origins,
			boolean credentials, int maxAge) {
		this.headers = headers;
		this.methods = methods;
		this.origins = origins;
		this.credentials = credentials;
		this.maxAge = maxAge;
	}

	public String[] getHeaders() {
		return headers == null ? EMPTY : headers.clone();
	}

	public Cors setHeaders(String... headers) {
		if (readyOnly) {
			throw new NotSupportedException("setMaxAge");
		}

		this.headers = headers;
		return this;
	}

	public String[] getMethods() {
		return methods == null ? EMPTY : headers.clone();
	}

	public Cors setMethods(String... methods) {
		if (readyOnly) {
			throw new NotSupportedException("setMaxAge");
		}

		this.methods = methods;
		return this;
	}

	public String[] getOrigins() {
		return origins == null ? EMPTY : origins.clone();
	}

	public void setOrigins(String... origins) {
		if (readyOnly) {
			throw new NotSupportedException("setMaxAge");
		}

		this.origins = origins;
	}

	public Boolean isCredentials() {
		return credentials;
	}

	public Cors setCredentials(Boolean credentials) {
		if (readyOnly) {
			throw new NotSupportedException("setMaxAge");
		}

		this.credentials = credentials;
		return this;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public Cors setMaxAge(int maxAge) {
		if (readyOnly) {
			throw new NotSupportedException("setMaxAge");
		}

		this.maxAge = maxAge;
		return this;
	}

	public final boolean isReadyOnly() {
		return readyOnly;
	}

	public Cors readyOnly() {
		this.readyOnly = true;
		return this;
	}

	@Override
	public Object clone() {
		return new Cors(headers, methods, origins, credentials, maxAge);
	}

	private void write(HttpHeaders headers, String headerName, String[] values,
			String defaultValue) {
		if (ArrayUtils.isEmpty(values)) {
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
		write(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, this.origins,
				"*");
		write(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, this.methods,
				"*");
		write(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, this.headers,
				"*");
		if (maxAge > 0) {
			headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, maxAge + "");
		}

		if (credentials != null) {
			headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
					credentials + "");
		}
	}
}
