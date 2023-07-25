package io.basc.framework.net.uri;

import io.basc.framework.util.Assert;
import io.basc.framework.util.collect.MultiValueMap;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UriComponents implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_ENCODING = "UTF-8";

	/** Captures URI template variable names */
	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	private final String scheme;

	private final String fragment;

	protected UriComponents(String scheme, String fragment) {
		this.scheme = scheme;
		this.fragment = fragment;
	}

	// Component getters
	public final String getScheme() {
		return this.scheme;
	}

	public final String getFragment() {
		return this.fragment;
	}

	public abstract String getSchemeSpecificPart();

	public abstract String getUserInfo();

	public abstract String getHost();

	public abstract int getPort();

	public abstract String getPath();

	public abstract List<String> getPathSegments();

	public abstract String getQuery();

	public abstract MultiValueMap<String, String> getQueryParams();

	/**
	 * Encode all URI components using their specific encoding rules, and returns
	 * the result as a new {@code UriComponents} instance. This method uses UTF-8 to
	 * encode.
	 * 
	 * @return the encoded URI components
	 */
	public final UriComponents encode() {
		try {
			return encode(DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException ex) {
			// should not occur
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Encode all URI components using their specific encoding rules, and returns
	 * the result as a new {@code UriComponents} instance.
	 * 
	 * @param encoding the encoding of the values contained in this map
	 * @return the encoded URI components
	 * @throws UnsupportedEncodingException if the given encoding is not supported
	 */
	public abstract UriComponents encode(String encoding) throws UnsupportedEncodingException;

	/**
	 * Replace all URI template variables with the values from a given map.
	 * <p>
	 * The given map keys represent variable names; the corresponding values
	 * represent variable values. The order of variables is not significant.
	 * 
	 * @param uriVariables the map of URI variables
	 * @return the expanded URI components
	 */
	public final UriComponents expand(Map<String, ?> uriVariables) {
		Assert.notNull(uriVariables, "'uriVariables' must not be null");
		return expandInternal(new MapTemplateVariables(uriVariables));
	}

	/**
	 * Replace all URI template variables with the values from a given array.
	 * <p>
	 * The given array represents variable values. The order of variables is
	 * significant.
	 * 
	 * @param uriVariableValues the URI variable values
	 * @return the expanded URI components
	 */
	public final UriComponents expand(Object... uriVariableValues) {
		Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");
		return expandInternal(new VarArgsTemplateVariables(uriVariableValues));
	}

	/**
	 * Replace all URI template variables with the values from the given
	 * {@link UriTemplateVariables}.
	 * 
	 * @param uriVariables the URI template values
	 * @return the expanded URI components
	 */
	public final UriComponents expand(UriTemplateVariables uriVariables) {
		Assert.notNull(uriVariables, "'uriVariables' must not be null");
		return expandInternal(uriVariables);
	}

	/**
	 * Replace all URI template variables with the values from the given
	 * {@link UriTemplateVariables}
	 * 
	 * @param uriVariables URI template values
	 * @return the expanded URI components
	 */
	abstract UriComponents expandInternal(UriTemplateVariables uriVariables);

	public abstract UriComponents normalize();

	public abstract String toUriString();

	public abstract URI toUri();

	@Override
	public final String toString() {
		return toUriString();
	}

	protected abstract void copyToUriComponentsBuilder(UriComponentsBuilder builder);

	// Static expansion helpers

	static String expandUriComponent(String source, UriTemplateVariables uriVariables) {
		if (source == null) {
			return null;
		}
		if (source.indexOf('{') == -1) {
			return source;
		}
		if (source.indexOf(':') != -1) {
			source = sanitizeSource(source);
		}
		Matcher matcher = NAMES_PATTERN.matcher(source);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String match = matcher.group(1);
			String variableName = getVariableName(match);
			Object variableValue = uriVariables.getValue(variableName);
			if (UriTemplateVariables.SKIP_VALUE.equals(variableValue)) {
				continue;
			}
			String variableValueString = getVariableValueAsString(variableValue);
			String replacement = Matcher.quoteReplacement(variableValueString);
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Remove nested "{}" such as in URI vars with regular expressions.
	 */
	private static String sanitizeSource(String source) {
		int level = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : source.toCharArray()) {
			if (c == '{') {
				level++;
			}
			if (c == '}') {
				level--;
			}
			if (level > 1 || (level == 1 && c == '}')) {
				continue;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	private static String getVariableName(String match) {
		int colonIdx = match.indexOf(':');
		return (colonIdx != -1 ? match.substring(0, colonIdx) : match);
	}

	private static String getVariableValueAsString(Object variableValue) {
		return (variableValue != null ? variableValue.toString() : "");
	}

	/**
	 * Defines the contract for URI Template variables
	 * 
	 * @see HierarchicalUriComponents#expand
	 */
	public interface UriTemplateVariables {

		Object SKIP_VALUE = UriTemplateVariables.class;

		/**
		 * Get the value for the given URI variable name. If the value is {@code null},
		 * an empty String is expanded. If the value is {@link #SKIP_VALUE}, the URI
		 * variable is not expanded.
		 * 
		 * @param name the variable name
		 * @return the variable value, possibly {@code null} or {@link #SKIP_VALUE}
		 */
		Object getValue(String name);
	}

	/**
	 * URI template variables backed by a map.
	 */
	private static class MapTemplateVariables implements UriTemplateVariables {

		private final Map<String, ?> uriVariables;

		public MapTemplateVariables(Map<String, ?> uriVariables) {
			this.uriVariables = uriVariables;
		}

		public Object getValue(String name) {
			if (!this.uriVariables.containsKey(name)) {
				throw new IllegalArgumentException("Map has no value for '" + name + "'");
			}
			return this.uriVariables.get(name);
		}
	}

	/**
	 * URI template variables backed by a variable argument array.
	 */
	private static class VarArgsTemplateVariables implements UriTemplateVariables {

		private final Iterator<Object> valueIterator;

		public VarArgsTemplateVariables(Object... uriVariableValues) {
			this.valueIterator = Arrays.asList(uriVariableValues).iterator();
		}

		public Object getValue(String name) {
			if (!this.valueIterator.hasNext()) {
				throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
			}
			return this.valueIterator.next();
		}
	}

}
