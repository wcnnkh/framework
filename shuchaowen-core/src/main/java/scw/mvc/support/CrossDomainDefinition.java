package scw.mvc.support;

import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;

public final class CrossDomainDefinition {
	private static String DEFAULT_HEADERS = "X-Requested-With,Content-Type,X-Forwarded-For,Cookie";
	public static final CrossDomainDefinition DEFAULT = new CrossDomainDefinition("*", DEFAULT_HEADERS, "*", false, -1);

	private final String origin;
	private final String headers;
	private final String methods;
	private final boolean credentials;
	private final int maxAge;

	public CrossDomainDefinition(String origin, String headers, String methods, boolean credentials, int maxAge) {
		this.origin = origin;
		this.headers = headers;
		this.methods = methods;
		this.credentials = credentials;
		this.maxAge = maxAge;
	}

	public CrossDomainDefinition(PropertyFactory propertyFactory) {
		String origin = propertyFactory.getProperty("mvc.http.cross-domain.origin");
		this.origin = StringUtils.isEmpty(origin) ? "*" : origin;

		String headers = propertyFactory.getProperty("mvc.http.cross-domain.headers");
		this.headers = StringUtils.isEmpty(headers) ? DEFAULT_HEADERS : headers;

		String appendHeaders = propertyFactory.getProperty("mvc.http.cross-domain.headers.append");
		if (!StringUtils.isEmpty(appendHeaders)) {
			headers = StringUtils.isEmpty(headers) ? appendHeaders : (headers + appendHeaders);
		}

		String methods = propertyFactory.getProperty("mvc.http.cross-domain.methods");
		this.methods = StringUtils.isEmpty(methods) ? "*" : methods;
		this.credentials = StringUtils.parseBoolean(propertyFactory.getProperty("mvc.http.cross-domain.credentials"));
		this.maxAge = StringUtils.parseInt(propertyFactory.getProperty("mvc.http.cross-domain.maxAge"), -1);
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
