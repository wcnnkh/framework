package io.basc.framework.websocket;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collection.CollectionUtils;
import io.basc.framework.util.collection.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class WebSocketExtension {

	private final String name;

	private final Map<String, String> parameters;

	/**
	 * Create a WebSocketExtension with the given name.
	 * 
	 * @param name the name of the extension
	 */
	public WebSocketExtension(String name) {
		this(name, null);
	}

	/**
	 * Create a WebSocketExtension with the given name and parameters.
	 * 
	 * @param name       the name of the extension
	 * @param parameters the parameters
	 */
	public WebSocketExtension(String name, Map<String, String> parameters) {
		Assert.hasLength(name, "Extension name must not be empty");
		this.name = name;
		if (!CollectionUtils.isEmpty(parameters)) {
			Map<String, String> map = new LinkedCaseInsensitiveMap<String>(parameters.size(), Locale.ENGLISH);
			map.putAll(parameters);
			this.parameters = Collections.unmodifiableMap(map);
		} else {
			this.parameters = Collections.emptyMap();
		}
	}

	public String getName() {
		return this.name;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		WebSocketExtension otherExt = (WebSocketExtension) other;
		return (this.name.equals(otherExt.name) && this.parameters.equals(otherExt.parameters));
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() * 31 + this.parameters.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.name);
		for (Entry<String, String> entry : parameters.entrySet()) {
			str.append(";").append(entry.getKey()).append("=").append(entry.getValue());
		}
		return str.toString();
	}

	/**
	 * Parse the given, comma-separated string into a list of
	 * {@code WebSocketExtension} objects.
	 * <p>
	 * This method can be used to parse a "Sec-WebSocket-Extension" header.
	 * 
	 * @param extensions the string to parse
	 * @return the list of extensions
	 * @throws IllegalArgumentException if the string cannot be parsed
	 */
	public static List<WebSocketExtension> parseExtensions(String extensions) {
		if (StringUtils.hasText(extensions)) {
			String[] tokens = StringUtils.tokenizeToArray(extensions, ",");
			List<WebSocketExtension> result = new ArrayList<WebSocketExtension>(tokens.length);
			for (String token : tokens) {
				result.add(parseExtension(token));
			}
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	private static WebSocketExtension parseExtension(String extension) {
		if (extension.contains(",")) {
			throw new IllegalArgumentException("Expected single extension value: [" + extension + "]");
		}
		String[] parts = StringUtils.tokenizeToArray(extension, ";");
		String name = parts[0].trim();

		Map<String, String> parameters = null;
		if (parts.length > 1) {
			parameters = new LinkedHashMap<String, String>(parts.length - 1);
			for (int i = 1; i < parts.length; i++) {
				String parameter = parts[i];
				int eqIndex = parameter.indexOf('=');
				if (eqIndex != -1) {
					String attribute = parameter.substring(0, eqIndex);
					String value = parameter.substring(eqIndex + 1, parameter.length());
					parameters.put(attribute, value);
				}
			}
		}

		return new WebSocketExtension(name, parameters);
	}

}
