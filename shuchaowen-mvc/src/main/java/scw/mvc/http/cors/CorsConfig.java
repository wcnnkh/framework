package scw.mvc.http.cors;

import scw.core.utils.StringUtils;
import scw.util.value.property.PropertyFactory;

public final class CorsConfig {
	private static String DEFAULT_HEADERS = "X-Requested-With,Content-Type,X-Forwarded-For,Cookie";
	public static final CorsConfig DEFAULT = new CorsConfig("*", DEFAULT_HEADERS, "*", false, -1);

	private final String origin;
	private final String headers;
	private final String methods;
	private final boolean credentials;
	private final int maxAge;

	public CorsConfig(String origin, String headers, String methods, boolean credentials, int maxAge) {
		this.origin = origin;
		this.headers = headers;
		this.methods = methods;
		this.credentials = credentials;
		this.maxAge = maxAge;
	}

	public CorsConfig(PropertyFactory propertyFactory) {
		String origin = propertyFactory.getString("mvc.http.cross-domain.origin");
		this.origin = StringUtils.isEmpty(origin) ? "*" : origin;

		String headers = propertyFactory.getString("mvc.http.cross-domain.headers");
		this.headers = StringUtils.isEmpty(headers) ? DEFAULT_HEADERS : headers;

		String appendHeaders = propertyFactory.getString("mvc.http.cross-domain.headers.append");
		if (!StringUtils.isEmpty(appendHeaders)) {
			headers = StringUtils.isEmpty(headers) ? appendHeaders : (headers + appendHeaders);
		}

		String methods = propertyFactory.getString("mvc.http.cross-domain.methods");
		this.methods = StringUtils.isEmpty(methods) ? "*" : methods;
		this.credentials = propertyFactory.getBooleanValue("mvc.http.cross-domain.credentials");
		this.maxAge = StringUtils.parseInt(propertyFactory.getString("mvc.http.cross-domain.maxAge"), -1);
	}

	public String getOrigin() {
		return origin;
	}

	public String getHeaders() {
		return headers;
	}

	public String getMethods() {
		return methods;
	}

	public boolean isCredentials() {
		return credentials;
	}

	public int getMaxAge() {
		return maxAge;
	}
}
