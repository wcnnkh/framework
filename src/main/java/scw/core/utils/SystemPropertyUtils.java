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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Constants;
import scw.core.StringFormatSystemProperties;
import scw.io.FileUtils;

public abstract class SystemPropertyUtils {
	private static final String WEB_ROOT = "web.root";
	private static final String WORK_PATH_PROPERTY_NAME = "scw_work_path";
	private static final String DEFAULT_WORK_PATH_DIR = "public,www";
	private static volatile String workPath;
	private static final String SYSTEM_ID_PROPERTY = "private.system.id";
	/**
	 * 私有的Properties
	 */
	private static final ConcurrentHashMap<String, String> PRIVATE_PROPERTIES = new ConcurrentHashMap<String, String>();

	static {
		String path = getProperty("scw.properties.private");
		if (path == null) {
			path = "classpath:/private.properties";
		}

		if (ResourceUtils.isExist(path)) {
			Properties properties = PropertiesUtils.getProperties(path);
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

	public static String getProperty(String key) {
		String v = getPrivateProperty(key);
		if (v == null) {
			v = System.getProperty(key);
		}

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

	public static void setProperty(String key, String value) {
		setProperty(key, value, true);
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

	public static void clearProperty(String key) {
		clearPrivateProperty(key);
		System.clearProperty(key);
	}

	public static void setWorkPath(String path, boolean system) {
		if (path == null) {
			return;
		}

		if (system) {
			SystemPropertyUtils.setProperty(WORK_PATH_PROPERTY_NAME, path);
		} else {
			workPath = new String(path);
		}
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
		if (path != null) {
			return path;
		}

		if (workPath == null) {
			synchronized (SystemPropertyUtils.class) {
				if (workPath == null) {
					URL url = ResourceUtils.getClassPathURL();
					if (url == null) {
						path = getDefaultWorkPath();
					} else {
						File file = new File(url.getPath());
						if (file.isFile()) {
							path = getUserDir();
						} else {
							file = file.getParentFile();
							if (file.getName().equals("WEB-INF")) {
								path = file.getParent();
							} else {
								file = file.getParentFile();
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
					workPath = path;
				}
			}
		}
		return workPath == null ? null : new String(workPath);
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
				systemOnlyId = scw.core.Base64.encode((getUserDir() + "&" + ResourceUtils.getClassPathURL())
						.getBytes(Constants.DEFAULT_CHARSET_NAME));
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
}
