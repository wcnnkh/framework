package io.basc.framework.net.uri;

import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

final class OpaqueUriComponents extends UriComponents {
	private static final long serialVersionUID = 1L;

	private static final MultiValueMap<String, String> QUERY_PARAMS_NONE = new LinkedMultiValueMap<String, String>(0);

	private final String ssp;

	OpaqueUriComponents(String scheme, String schemeSpecificPart, String fragment) {
		super(scheme, fragment);
		this.ssp = schemeSpecificPart;
	}

	@Override
	public String getSchemeSpecificPart() {
		return this.ssp;
	}

	@Override
	public String getUserInfo() {
		return null;
	}

	@Override
	public String getHost() {
		return null;
	}

	@Override
	public int getPort() {
		return -1;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public List<String> getPathSegments() {
		return Collections.emptyList();
	}

	@Override
	public String getQuery() {
		return null;
	}

	@Override
	public MultiValueMap<String, String> getQueryParams() {
		return QUERY_PARAMS_NONE;
	}

	@Override
	public UriComponents encode(String encoding) throws UnsupportedEncodingException {
		return this;
	}

	@Override
	protected UriComponents expandInternal(UriTemplateVariables uriVariables) {
		String expandedScheme = expandUriComponent(getScheme(), uriVariables);
		String expandedSsp = expandUriComponent(getSchemeSpecificPart(), uriVariables);
		String expandedFragment = expandUriComponent(getFragment(), uriVariables);
		return new OpaqueUriComponents(expandedScheme, expandedSsp, expandedFragment);
	}

	@Override
	public UriComponents normalize() {
		return this;
	}

	@Override
	public String toUriString() {
		StringBuilder uriBuilder = new StringBuilder();

		if (getScheme() != null) {
			uriBuilder.append(getScheme());
			uriBuilder.append(':');
		}
		if (this.ssp != null) {
			uriBuilder.append(this.ssp);
		}
		if (getFragment() != null) {
			uriBuilder.append('#');
			uriBuilder.append(getFragment());
		}

		return uriBuilder.toString();
	}

	@Override
	public URI toUri() {
		try {
			return new URI(getScheme(), this.ssp, getFragment());
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
		}
	}

	@Override
	protected void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
		builder.scheme(getScheme());
		builder.schemeSpecificPart(getSchemeSpecificPart());
		builder.fragment(getFragment());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OpaqueUriComponents)) {
			return false;
		}

		OpaqueUriComponents other = (OpaqueUriComponents) obj;
		return ObjectUtils.equals(getScheme(), other.getScheme()) && ObjectUtils.equals(this.ssp, other.ssp)
				&& ObjectUtils.equals(getFragment(), other.getFragment());

	}

	@Override
	public int hashCode() {
		int result = ObjectUtils.hashCode(getScheme());
		result = 31 * result + ObjectUtils.hashCode(this.ssp);
		result = 31 * result + ObjectUtils.hashCode(getFragment());
		return result;
	}

}
