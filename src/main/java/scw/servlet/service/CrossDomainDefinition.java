package scw.servlet.service;

import scw.servlet.annotation.CrossDomain;

public final class CrossDomainDefinition {
	private final String origin;
	private final int maxAge;
	private final String headers;
	private final boolean credentials;

	public CrossDomainDefinition(CrossDomain crossDomain) {
		this.origin = crossDomain.origin();
		this.maxAge = crossDomain.maxAge();
		this.headers = crossDomain.headers();
		this.credentials = crossDomain.credentials();
	}

	public String getOrigin() {
		return origin;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public String getHeaders() {
		return headers;
	}

	public boolean isCredentials() {
		return credentials;
	}
}
