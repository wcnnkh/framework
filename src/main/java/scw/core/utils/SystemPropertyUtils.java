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
import java.net.URL;

import scw.core.Constants;
import scw.core.StringFormatSystemProperties;
import scw.core.utils.PropertyPlaceholderHelper.PlaceholderResolver;
import scw.io.FileUtils;

/**
 * Helper class for resolving placeholders in texts. Usually applied to file
 * paths.
 *
 * <p>
 * A text may contain {@code $ ...} placeholders, to be resolved as system
 * properties: e.g. {@code $ user.dir} . Default values can be supplied using
 * the ":" separator between key and value.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Dave Syer
 * @since 1.2.5
 * @see #PLACEHOLDER_PREFIX
 * @see #PLACEHOLDER_SUFFIX
 * @see System#getProperty(String)
 */
public abstract class SystemPropertyUtils {

	/** Prefix for system property placeholders: "${" */
	private static final String PLACEHOLDER_PREFIX = "${";

	/** Suffix for system property placeholders: "}" */
	private static final String PLACEHOLDER_SUFFIX = "}";

	/** Value separator for system property placeholders: ":" */
	private static final String VALUE_SEPARATOR = ":";

	public static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper(
			PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

	public static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper(
			PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);

	private static final StringFormatSystemProperties STRING_FORMAT_SYSTEM_PROPERTIES = new StringFormatSystemProperties(
			"{", "}");

	private static final String WEB_ROOT = "web.root";
	private static final String WORK_PATH_PROPERTY_NAME = "scw_work_path";
	private static final String DEFAULT_WORK_PATH_DIR = "public,www";

	/**
	 * Resolve ${...} placeholders in the given text, replacing them with
	 * corresponding system property values.
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
	 * Resolve ${...} placeholders in the given text, replacing them with
	 * corresponding system property values. Unresolvable placeholders with no
	 * default value are ignored and passed through unchanged if the flag is set
	 * to true.
	 * 
	 * @param text
	 *            the String to resolve
	 * @param ignoreUnresolvablePlaceholders
	 *            flag to determine is unresolved placeholders are ignored
	 * @return the resolved String
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * @throws IllegalArgumentException
	 *             if there is an unresolvable placeholder and the flag is false
	 */
	public static String resolvePlaceholders(String text,
			boolean ignoreUnresolvablePlaceholders) {
		PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper
				: strictHelper);
		return helper.replacePlaceholders(text,
				new SystemPropertyPlaceholderResolver(text));
	}

	private static class SystemPropertyPlaceholderResolver implements
			PlaceholderResolver {

		private final String text;

		public SystemPropertyPlaceholderResolver(String text) {
			this.text = text;
		}

		public String resolvePlaceholder(String placeholderName) {
			try {
				return getProperty(placeholderName);
			} catch (Throwable ex) {
				System.err.println("Could not resolve placeholder '"
						+ placeholderName + "' in [" + this.text
						+ "] as system property: " + ex);
				return null;
			}
		}
	}

	public static String getProperty(String key) {
		String v = System.getProperty(key);
		if (v == null) {
			v = System.getenv(key);
		}

		if (v == null) {
			if (WEB_ROOT.equalsIgnoreCase(key)) {
				return getWorkPath();
			}
		}
		return v;
	}

	public static void setWorkPath(String path) {
		System.setProperty(WORK_PATH_PROPERTY_NAME, path);
	}

	private static String getDefaultWorkPath() {
		for (String name : StringUtils.commonSplit(DEFAULT_WORK_PATH_DIR)) {
			File file = new File(getUserDir() + File.separator + name);
			if (file.exists()) {
				return file.getPath();
			}
		}

		return getUserDir();
	}

	/**
	 * 如果返回空就说明不存在WEB-INF目录
	 * 
	 * @return
	 */
	public static String getWorkPath() {
		String path = getProperty(WORK_PATH_PROPERTY_NAME);
		if (path == null) {
			URL url = ResourceUtils.getClassPathURL();
			if (url == null) {
				path = getDefaultWorkPath();
			} else {
				File file = new File(url.getPath());
				if (file.isFile()) {
					path = getUserDir();
				} else {
					file = file.getParentFile();
					if (file != null) {
						file = file.getParentFile();
					}

					if (file != null) {
						file = FileUtils.searchDirectory(file, "WEB-INF");
						if (file != null) {
							path = file.getParent();
						}
					}
				}

				if (path == null) {
					path = getDefaultWorkPath();
				}
			}
			setWorkPath(path);
		}
		return path;
	}

	public static String format(String text) {
		return STRING_FORMAT_SYSTEM_PROPERTIES.format(text);
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
		try {
			return scw.core.Base64.encode((getUserDir() + "&" + ResourceUtils
					.getClassPathURL())
					.getBytes(Constants.DEFAULT_CHARSET_NAME));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getMavenHome() {
		return getProperty("maven.home");
	}
}
