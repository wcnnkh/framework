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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Constants;
import scw.core.StringFormatSystemProperties;
import scw.core.resource.DefaultResourceLookup;
import scw.core.resource.ResourceOperations;
import scw.core.resource.SystemPropertyMultiSuffixResourceOperations;
import scw.io.FileUtils;

public final class SystemPropertyUtils {
	private static final String WEB_ROOT = "web.root";
	private static final String SYSTEM_ID_PROPERTY = "private.system.id";
	private static final String CLASSES_DIRECTORY = "classes.directory";

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
				System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text
						+ "] as system property: " + ex);
				return null;
			}
		}
	}

	private SystemPropertyUtils() {
	};

	/**
	 * 私有的Properties
	 */
	private static final ConcurrentHashMap<String, String> PRIVATE_PROPERTIES = new ConcurrentHashMap<String, String>();

	static {
		String path = getProperty("scw.properties.private");
		if (path == null) {
			path = "/private.properties";
		}

		if (getSystemResourceOperations().isExist(path)) {
			Properties properties = getSystemResourceOperations().getProperties(path);
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Object key = entry.getKey();
				if (key == null) {
					continue;
				}

				Object value = entry.getValue();
				if (value == null) {
					continue;
				}

				PRIVATE_PROPERTIES.put(key.toString(), value.toString());
			}
		}
	}

	/**
	 * 获取无任何class loader依赖的资源操作
	 * 
	 * @return
	 */
	private static ResourceOperations getSystemResourceOperations() {
		return new SystemPropertyMultiSuffixResourceOperations(new DefaultResourceLookup(getWorkPath(), false)) {
			@Override
			protected String getProperty(String key) {
				return getSystemProperty(key);
			}
		};
	}

	public static String getClassesDirectory() {
		String path = getProperty(CLASSES_DIRECTORY);
		if (path != null) {
			return path;
		}

		File file = new File(getWorkPath());
		if (!file.exists() || !file.isDirectory()) {
			return null;
		}

		File webInf = null;
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory() && f.getName().equals("WEB-INF")) {
					webInf = f;
				}
			}
		}

		if (webInf == null) {
			return null;
		}

		files = webInf.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory() && f.getName().equals("classes")) {
					setClassesDirectory(f.getPath());
				}
			}
		}
		return getProperty(CLASSES_DIRECTORY);
	}

	public static void setClassesDirectory(String directory) {
		if (StringUtils.isEmpty(directory)) {
			return;
		}

		setPrivateProperty(CLASSES_DIRECTORY, directory);
	}

	public static String getSystemProperty(String key) {
		String v = getPrivateProperty(key);
		if (v == null) {
			v = System.getProperty(key);
		}

		if (v == null) {
			v = System.getenv(key);
		}

		return v;
	}

	public static String getProperty(String key) {
		String v = getSystemProperty(key);
		if (v == null) {
			if (WEB_ROOT.equalsIgnoreCase(key)) {
				return getWorkPath();
			}
		}
		return v;
	}

	public static void setProperty(String key, String value) {
		setProperty(key, value, false);
	}

	public static void setSystemProperty(String key, String value) {
		System.setProperty(key, value);
	}

	public static void setProperty(String key, String value, boolean system) {
		if (system) {
			setSystemProperty(key, value);
		} else {
			setPrivateProperty(key, value);
		}
	}

	public static void clearSystemProperty(String key) {
		System.clearProperty(key);
	}

	public static void clearProperty(String key) {
		clearPrivateProperty(key);
		clearSystemProperty(key);
	}

	public static void setWorkPath(String path) {
		if (path == null) {
			return;
		}

		SystemPropertyUtils.setPrivateProperty(WEB_ROOT, path);
	}

	public static String getWorkPath() {
		String path = getSystemProperty(WEB_ROOT);
		if (path != null) {
			return path;
		}

		if (path == null) {
			File file = FileUtils.searchDirectory(new File(getUserDir()), "WEB-INF");
			path = file == null ? getUserDir() : file.getParent();
			setWorkPath(path);
		}
		return path;
	}

	public static String format(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		return StringFormatSystemProperties.formatText(StringFormatSystemProperties.formatEL(text));
	}

	/**
	 * 获取环境变量分割符
	 * 
	 * @return
	 */
	public static String getPathSeparator() {
		return getProperty("path.separator");
	}

	public static String getJavaClassPath() {
		return getProperty("java.class.path");
	}

	public static String[] getJavaClassPathArray() {
		String classPath = getJavaClassPath();
		if (StringUtils.isEmpty(classPath)) {
			return null;
		}

		return StringUtils.split(classPath, getPathSeparator());
	}

	public static String getUserDir() {
		return getProperty("user.dir");
	}

	public static String getSystemOnlyId() {
		String systemOnlyId = getPrivateProperty(SYSTEM_ID_PROPERTY);
		if (StringUtils.isEmpty(systemOnlyId)) {
			try {
				systemOnlyId = scw.core.Base64
						.encode((getUserDir() + "&" + getWorkPath()).getBytes(Constants.DEFAULT_CHARSET_NAME));
				if (systemOnlyId.endsWith("==")) {
					systemOnlyId = systemOnlyId.substring(0, systemOnlyId.length() - 2);
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			setPrivateProperty(SYSTEM_ID_PROPERTY, systemOnlyId);
		}
		return systemOnlyId;
	}

	public static String getMavenHome() {
		return getProperty("maven.home");
	}

	public static String getPrivateProperty(String key) {
		return PRIVATE_PROPERTIES.get(key);
	}

	public static void setPrivateProperty(String key, String value) {
		PRIVATE_PROPERTIES.put(key, value);
	}

	public static void clearPrivateProperty(String key) {
		PRIVATE_PROPERTIES.remove(key);
	}

	public static String getTempDirectoryPath() {
		return getProperty("java.io.tmpdir");
	}

	public static <T> T[] getArrayProperty(Class<T> componentType, String key, T[] defaultValue) {
		return getArrayProperty(componentType, key, defaultValue, StringUtils.DEFAULT_SPLIT_CHARS);
	}

	public static <T> T[] getArrayProperty(Class<T> componentType, String key, T[] defaultValue, char[] splitFilter) {
		String value = getProperty(key);
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
		String value = getProperty(key);
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
