package io.basc.framework.net.uri;

import java.net.URI;
import java.util.Map;

public interface UriTemplateHandler {

	/**
	 * Expand the given URI template from a map of URI variables.
	 * 
	 * @param uriTemplate  the URI template string
	 * @param uriVariables the URI variables
	 * @return the resulting URI
	 */
	URI expand(String uriTemplate, Map<String, ?> uriVariables);

	/**
	 * Expand the given URI template from an array of URI variables.
	 * 
	 * @param uriTemplate  the URI template string
	 * @param uriVariables the URI variable values
	 * @return the resulting URI
	 */
	URI expand(String uriTemplate, Object... uriVariables);

}
