/*
\ * Copyright 2002-2012 the original author or authors.
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

package scw.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Consumer;
import scw.core.Converter;
import scw.core.PropertyFactory;
import scw.core.SystemPropertyFactory;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.io.IOUtils;
import scw.logger.LoggerUtils;

/**
 * 资源工具
 * 
 * @author scw
 */
public final class ResourceUtils {
	private ResourceUtils() {
	};

	private static final boolean CACHE_ENABLE = StringUtils
			.parseBoolean(SystemPropertyUtils.getProperty("resource.cache.enable"));
	private static final ResourceLookup RESOURCE_LOOKUP;

	static {
		MultiResourceLookup multiResourceLookup = new MultiResourceLookup();
		String[] names = SystemPropertyUtils.getArrayProperty(String.class, "resource.lookup", new String[] {});
		for (String name : names) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			ResourceLookup lookup = InstanceUtils.getInstance(name);
			if (lookup == null) {
				LoggerUtils.warn(ResourceUtils.class, "Cannot instantiate, ignored:{}", name);
				continue;
			}

			multiResourceLookup.addResourceLookup(lookup);
		}

		multiResourceLookup.addResourceLookup(new LocalResourceLookup());
		RESOURCE_LOOKUP = CACHE_ENABLE ? new CacheResourceLookup(multiResourceLookup) : multiResourceLookup;
	}

	private static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";
	private static final String RESOURCE_SUFFIX = "scw_res_suffix";

	/**
	 * 是否开启资源缓存
	 * @return
	 */
	public static boolean isCacheEnable() {
		return CACHE_ENABLE;
	}

	public static URL getClassPathURL() {
		URL url = ResourceUtils.class.getResource("/");
		if (url == null) {
			ProtectionDomain protectionDomain = ResourceUtils.class.getProtectionDomain();
			if (protectionDomain != null) {
				CodeSource codeSource = protectionDomain.getCodeSource();
				if (codeSource != null) {
					url = codeSource.getLocation();
				}
			}
		}
		return url;
	}

	public static void setResourceSuffix(String suffix) {
		if (StringUtils.isEmpty(suffix)) {
			return;
		}

		SystemPropertyUtils.setPrivateProperty(RESOURCE_SUFFIX, suffix);
	}

	private static String getTestFileName(String fileName, String str) {
		int index = fileName.lastIndexOf(".");
		if (index == -1) {// 不存在
			return fileName + str;
		} else {
			return fileName.substring(0, index) + str + fileName.substring(index);
		}
	}

	private static List<String> getResourceNameList(String resourceName) {
		String value = SystemPropertyUtils.getProperty(RESOURCE_SUFFIX);
		if (value == null) {
			value = SystemPropertyUtils.getProperty(CONFIG_SUFFIX);
		}

		if (value == null) {
			return Arrays.asList(resourceName);
		}

		List<String> list = new LinkedList<String>();
		String[] arr = StringUtils.commonSplit(value);
		for (String name : arr) {
			list.add(getTestFileName(resourceName, name));
		}
		list.add(resourceName);
		return list;
	}

	public static void consumterInputStream(String resource, Consumer<InputStream> consumer) {
		Collection<String> resourceNames = getResourceNameList(resource);
		for (String name : resourceNames) {
			if (RESOURCE_LOOKUP.lookup(name, consumer)) {
				return;
			}
		}
		throw new NotFoundException(resource);
	}

	public static boolean isExist(String resource) {
		if (StringUtils.isEmpty(resource)) {
			return false;
		}

		Collection<String> resourceNames = getResourceNameList(resource);
		for (String name : resourceNames) {
			if (RESOURCE_LOOKUP.lookup(name, null)) {
				return true;
			}
		}
		return false;
	}

	public static <T> T getResource(String resource, final Converter<InputStream, T> converter) {
		if (StringUtils.isEmpty(resource)) {
			return null;
		}

		Collection<String> resourceNames = getResourceNameList(resource);
		for (String name : resourceNames) {
			T value = getResource(name, converter, RESOURCE_LOOKUP);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public static <T> T getResource(String resource, Converter<InputStream, T> converter,
			ResourceLookup resourceLookup) {
		InputStreamConvertConsumer<T> inputStreamConvertConsumer = new InputStreamConvertConsumer<T>(converter);
		resourceLookup.lookup(resource, inputStreamConvertConsumer);
		return inputStreamConvertConsumer.getValue();
	}

	public static Properties getProperties(final String resource, final String charsetName,
			PropertyFactory propertyFactory) {
		return getProperties(resource, charsetName, propertyFactory, false);
	}

	public static Properties getProperties(final String resource, final String charsetName,
			PropertyFactory propertyFactory, boolean cache) {
		List<String> resourceNameList = getResourceNameList(resource);
		ListIterator<String> iterator = resourceNameList.listIterator(resourceNameList.size());
		Properties properties = new Properties();
		while (iterator.hasPrevious()) {
			String name = iterator.previous();
			RESOURCE_LOOKUP.lookup(name, new LoadPropertiesConsumer(properties, name, charsetName));
		}

		if (propertyFactory == null) {
			return properties;
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			entry.setValue(FormatUtils.format(value.toString(), propertyFactory, true));
		}
		return properties;
	}

	public static Properties getProperties(final String path) {
		return getProperties(path, (String) null);
	}

	public static Properties getProperties(final String path, final String charsetName) {
		return getProperties(path, charsetName, SystemPropertyFactory.INSTANCE);
	}

	public static Properties getProperties(final String path, final String charsetName, boolean cache) {
		return getProperties(path, charsetName, SystemPropertyFactory.INSTANCE, cache);
	}

	public static Properties getProperties(final String path, PropertyFactory propertyFactory) {
		return getProperties(path, null, propertyFactory);
	}

	public static List<String> getFileContentLineList(String path, final String charsetName) {
		return getResource(path, new Converter<InputStream, List<String>>() {

			public List<String> convert(InputStream inputStream) {
				try {
					return IOUtils.readLines(inputStream, charsetName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public static String getFileContent(String path, final String charsetName) {
		return getResource(path, new Converter<InputStream, String>() {

			public String convert(InputStream inputStream) {
				return IOUtils.readContent(inputStream, charsetName);
			}
		});
	}
}
