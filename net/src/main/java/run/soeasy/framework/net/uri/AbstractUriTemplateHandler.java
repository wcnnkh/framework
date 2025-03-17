package run.soeasy.framework.net.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import run.soeasy.framework.util.Assert;

public abstract class AbstractUriTemplateHandler implements UriTemplateHandler {

	private String baseUrl;

	private final Map<String, Object> defaultUriVariables = new HashMap<String, Object>();

	/**
	 * Configure a base URL to prepend URI templates with. The base URL must have a
	 * scheme and host but may optionally contain a port and a path. The base URL
	 * must be fully expanded and encoded which can be done via
	 * {@link UriComponentsBuilder}.
	 * 
	 * @param baseUrl the base URL.
	 */
	public void setBaseUrl(String baseUrl) {
		if (baseUrl != null) {
			UriComponents uriComponents = UriComponentsBuilder.fromUriString(baseUrl).build();
			Assert.hasText(uriComponents.getScheme(), "'baseUrl' must have a scheme");
			Assert.hasText(uriComponents.getHost(), "'baseUrl' must have a host");
			Assert.isNull(uriComponents.getQuery(), "'baseUrl' cannot have a query");
			Assert.isNull(uriComponents.getFragment(), "'baseUrl' cannot have a fragment");
		}
		this.baseUrl = baseUrl;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	/**
	 * Configure default URI variable values to use with every expanded URI
	 * template. These default values apply only when expanding with a Map, and not
	 * with an array, where the Map supplied to {@link #expand(String, Map)} can
	 * override the default values.
	 * 
	 * @param defaultUriVariables the default URI variable values
	 */
	public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
		this.defaultUriVariables.clear();
		if (defaultUriVariables != null) {
			this.defaultUriVariables.putAll(defaultUriVariables);
		}
	}

	public Map<String, ?> getDefaultUriVariables() {
		return Collections.unmodifiableMap(this.defaultUriVariables);
	}

	public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
		if (!getDefaultUriVariables().isEmpty()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll(getDefaultUriVariables());
			map.putAll(uriVariables);
			uriVariables = map;
		}
		URI url = expandInternal(uriTemplate, uriVariables);
		return insertBaseUrl(url);
	}

	public URI expand(String uriTemplate, Object... uriVariables) {
		URI url = expandInternal(uriTemplate, uriVariables);
		return insertBaseUrl(url);
	}

	protected abstract URI expandInternal(String uriTemplate, Map<String, ?> uriVariables);

	protected abstract URI expandInternal(String uriTemplate, Object... uriVariables);

	private URI insertBaseUrl(URI url) {
		try {
			String baseUrl = getBaseUrl();
			if (baseUrl != null && url.getHost() == null) {
				url = new URI(baseUrl + url.toString());
			}
			return url;
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Invalid URL after inserting base URL: " + url, ex);
		}
	}

}
