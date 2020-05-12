package scw.io.support;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.DefaultResourceLoader;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.lang.NestedRuntimeException;
import scw.util.ConcurrentReferenceHashMap;
import scw.util.FormatUtils;
import scw.value.property.PropertyFactory;

public class ResourceOperations extends DefaultResourceLoader {
	private static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";
	private static final String RESOURCE_SUFFIX = "scw_res_suffix";
	private final PropertyFactory propertyFactory;
	private final boolean cacheEnable;
	private final ConcurrentMap<String, Resource> resourceCache = new ConcurrentReferenceHashMap<String, Resource>();

	public ResourceOperations(PropertyFactory propertyFactory,
			boolean cacheEnable) {
		this.propertyFactory = propertyFactory;
		this.cacheEnable = cacheEnable;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public boolean isCacheEnable() {
		return cacheEnable;
	}

	protected String[] getResourceEnvironmentalNames() {
		return getPropertyFactory().getValue(RESOURCE_SUFFIX, String[].class,
				getPropertyFactory().getObject(CONFIG_SUFFIX, String[].class));
	}

	public List<String> getEnvironmentalResourceNameList(String resourceName) {
		String[] suffixs = getResourceEnvironmentalNames();
		String resourceNameToUse = getPropertyFactory().format(resourceName,
				true);
		if (ArrayUtils.isEmpty(suffixs)) {
			return Arrays.asList(resourceNameToUse);
		}

		List<String> list = new ArrayList<String>(suffixs.length + 1);
		for (String name : suffixs) {
			list.add(getEnvironmentalResourceName(resourceNameToUse, name));
		}
		list.add(resourceNameToUse);
		return list;
	}

	protected String getEnvironmentalResourceName(String resourceName,
			String evnironmental) {
		int index = resourceName.lastIndexOf(".");
		if (index == -1) {// 不存在
			return resourceName + evnironmental;
		} else {
			return resourceName.substring(0, index) + evnironmental
					+ resourceName.substring(index);
		}
	};

	public List<Resource> getResources(String resource) {
		List<String> nameList = getEnvironmentalResourceNameList(resource);
		if (nameList.size() <= 1) {
			for (String name : nameList) {
				Resource res = isCacheEnable() ? getResourceByCache(name)
						: super.getResource(name);
				if (res == null || !res.exists()) {
					return Collections.emptyList();
				}

				return Arrays.asList(res);
			}
			return Collections.emptyList();
		}

		List<Resource> resources = new ArrayList<Resource>(nameList.size());
		for (String name : nameList) {
			Resource res = super.getResource(name);
			if (res == null) {
				continue;
			}

			if (res.exists()) {
				resources.add(res);
			}
		}
		return resources;
	}

	protected Resource getResourceByCache(String location) {
		Resource resource = resourceCache.get(location);
		if (resource == null) {
			resource = super.getResource(location);
			Resource cache = resourceCache.putIfAbsent(location, resource);
			if (cache != null) {
				resource = cache;
			}

			if (resource == null) {
				resourceCache.putIfAbsent(location,
						Resource.NONEXISTENT_RESOURCE);
			}
		}
		return resource;
	}

	@Override
	public Resource getResource(String location) {
		List<String> nameList = getEnvironmentalResourceNameList(location);
		for (String name : nameList) {
			Resource resource = isCacheEnable() ? getResourceByCache(name)
					: super.getResource(name);
			if (resource == null || !resource.exists()) {
				continue;
			}

			return resource;
		}

		return null;
	}

	public void loadProperties(Properties properties, Resource resource,
			String charsetName) {
		if (!resource.exists()) {
			return;
		}

		InputStream is = null;
		try {
			is = resource.getInputStream();
			if (resource.getFilename().endsWith(".xml")) {
				properties.loadFromXML(is);
			} else {
				if (StringUtils.isEmpty(charsetName)) {
					properties.load(is);
				} else {
					Method method = ReflectionUtils.getMethod(Properties.class,
							"load", Reader.class);
					if (method == null) {
						FormatUtils.warn(getClass(), "jdk1.6及以上的版本才支持指定字符集: {}"
								+ resource.getDescription());
						properties.load(is);
					} else {
						InputStreamReader isr = null;
						try {
							isr = new InputStreamReader(is, charsetName);
							method.invoke(properties, isr);
						} finally {
							IOUtils.close(isr);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new NestedRuntimeException(resource.getDescription(), e);
		} finally {
			IOUtils.close(is);
		}
	}

	public void formatProperties(Properties properties,
			PropertyFactory propertyFactory) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			entry.setValue(propertyFactory.format(value.toString(), true));
		}
	}

	public Properties getProperties(String resource) {
		return getProperties(resource, null);
	}

	public Properties getProperties(String resource, String charsetName) {
		List<Resource> resources = getResources(resource);
		if (CollectionUtils.isEmpty(resources)) {
			return null;
		}

		Properties properties = new Properties();
		ListIterator<Resource> iterator = resources.listIterator(resources
				.size());
		while (iterator.hasPrevious()) {
			loadProperties(properties, iterator.previous(), charsetName);
		}
		return properties;
	}

	public String getContent(String resource, String charsetName) {
		return ResourceUtils.getContent(getResource(resource), charsetName);
	}

	public String getContent(String resource, Charset charset) {
		return ResourceUtils.getContent(getResource(resource), charset);
	}

	public List<String> getLines(String resource, Charset charset) {
		return ResourceUtils.getLines(getResource(resource), charset);
	}

	public List<String> getLines(String resource, String charsetName) {
		return ResourceUtils.getLines(getResource(resource), charsetName);
	}

	public boolean isExist(String resource) {
		Resource res = getResource(resource);
		return res != null && res.exists();
	}

	public Properties getFormattedProperties(String resource,
			String charsetName, PropertyFactory formatPropertyFactory) {
		Properties properties = getProperties(resource, charsetName);
		if (properties == null) {
			return properties;
		}

		formatProperties(properties, formatPropertyFactory);
		return properties;
	}

	public Properties getFormattedProperties(String resource,
			PropertyFactory formatPropertyFactory) {
		return getFormattedProperties(resource, null, formatPropertyFactory);
	}

	public Properties getFormattedProperties(String resource, String charsetName) {
		return getFormattedProperties(resource, charsetName,
				getPropertyFactory());
	}

	public Properties getFormattedProperties(String resource) {
		return getFormattedProperties(resource, getPropertyFactory());
	}

	public UnsafeByteArrayInputStream getInputStream(String resource) {
		byte[] data = getBytes(resource);
		if (data == null) {
			return null;
		}
		return new UnsafeByteArrayInputStream(data);
	}

	public byte[] getBytes(String resource) {
		return ResourceUtils.getBytes(getResource(resource));
	}
}
