/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.core.utils;

import scw.core.GlobalPropertyFactory;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class SystemPropertyUtils {
	private static Logger logger = LoggerUtils.getLogger(SystemPropertyUtils.class);
	/** Prefix for system property placeholders: "${" */
	public static final String PLACEHOLDER_PREFIX = "${";

	/** Suffix for system property placeholders: "}" */
	public static final String PLACEHOLDER_SUFFIX = "}";

	/** Value separator for system property placeholders: ":" */
	public static final String VALUE_SEPARATOR = ":";

	private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX,
			PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

	private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX,
			PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);

	/**
	 * Resolve {@code ${...}} placeholders in the given text, replacing them
	 * with corresponding system property values.
	 * 
	 * @param text
	 *            the String to resolve
	 * @return the resolved String
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * @throws IllegalArgumentException
	 *             if there is an unresolvable placeholder
	 */
	public static String resolvePlaceholders(String text) {
		return resolvePlaceholders(text, false);
	}

	/**
	 * Resolve {@code ${...}} placeholders in the given text, replacing them
	 * with corresponding system property values. Unresolvable placeholders with
	 * no default value are ignored and passed through unchanged if the flag is
	 * set to {@code true}.
	 * 
	 * @param text
	 *            the String to resolve
	 * @param ignoreUnresolvablePlaceholders
	 *            whether unresolved placeholders are to be ignored
	 * @return the resolved String
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * @throws IllegalArgumentException
	 *             if there is an unresolvable placeholder and the
	 *             "ignoreUnresolvablePlaceholders" flag is {@code false}
	 */
	public static String resolvePlaceholders(String text, boolean ignoreUnresolvablePlaceholders) {
		PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
		return helper.replacePlaceholders(text, new SystemPropertyPlaceholderResolver(text));
	}

	/**
	 * PlaceholderResolver implementation that resolves against system
	 * properties and system environment variables.
	 */
	private static class SystemPropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

		private final String text;

		public SystemPropertyPlaceholderResolver(String text) {
			this.text = text;
		}

		public String resolvePlaceholder(String placeholderName) {
			try {
				String propVal = System.getProperty(placeholderName);
				if (propVal == null) {
					// Fall back to searching the system environment.
					propVal = System.getenv(placeholderName);
				}
				return propVal;
			} catch (Throwable ex) {
				logger.error("Could not resolve placeholder '" + placeholderName + "' in [" + this.text
						+ "] as system property: " + ex);
				return null;
			}
		}
	}

	private SystemPropertyUtils() {
	};

	public static String[] getJavaClassPathArray() {
		String classPath = GlobalPropertyFactory.getInstance().getJavaClassPath();
		if (StringUtils.isEmpty(classPath)) {
			return null;
		}

		return StringUtils.split(classPath, GlobalPropertyFactory.getInstance().getPathSeparator());
	}

	public static <T> T[] getArrayProperty(Class<T> componentType, String key, T[] defaultValue) {
		return getArrayProperty(componentType, key, defaultValue, StringUtils.DEFAULT_SPLIT_CHARS);
	}

	public static <T> T[] getArrayProperty(Class<T> componentType, String key, T[] defaultValue, char[] splitFilter) {
		String value = GlobalPropertyFactory.getInstance().getString(key);
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}

		@SuppressWarnings("unchecked")
		T[] array = (T[]) StringUtils.parseArray(value, componentType, splitFilter);
		if (ArrayUtils.isEmpty(array)) {
			return defaultValue;
		}

		return array;
	}

	public static <T> T[] getArrayProperty(Class<T> componentType, String key, T[] defaultValue, String[] splitFilter) {
		String value = GlobalPropertyFactory.getInstance().getString(key);
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}

		@SuppressWarnings("unchecked")
		T[] array = (T[]) StringUtils.parseArray(value, componentType, splitFilter);
		if (ArrayUtils.isEmpty(array)) {
			return defaultValue;
		}

		return array;
	}
}
