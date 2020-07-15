package scw.io.support;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.io.DefaultResourceLoader;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.event.NonexistentObservableResource;
import scw.io.event.ObservableResource;
import scw.io.event.ObservableResourceEvent;
import scw.io.event.ObservableResourceEventListener;
import scw.io.event.ResourceEvent;
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

	public ResourceOperations(PropertyFactory propertyFactory, boolean cacheEnable) {
		this.propertyFactory = propertyFactory;
		this.cacheEnable = cacheEnable;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public boolean isCacheEnable() {
		return cacheEnable;
	}

	/**
	 * 可使用的资源别名，使用优先级从左到右
	 * 
	 * @return
	 */
	protected String[] getResourceEnvironmentalNames() {
		return getPropertyFactory().getValue(RESOURCE_SUFFIX, String[].class,
				getPropertyFactory().getObject(CONFIG_SUFFIX, String[].class));
	}

	/**
	 * 可使用的资源列表，使用优先级从左到右
	 * 
	 * @param resourceName
	 * @return
	 */
	public List<String> getEnvironmentalResourceNameList(String resourceName) {
		String[] suffixs = getResourceEnvironmentalNames();
		String resourceNameToUse = getPropertyFactory().format(resourceName, true);
		if (ArrayUtils.isEmpty(suffixs)) {
			return Arrays.asList(resourceNameToUse);
		}

		List<String> list = new ArrayList<String>(suffixs.length + 1);
		for (int i = suffixs.length - 1; i >= 0; i--) {
			list.add(getEnvironmentalResourceName(resourceNameToUse, suffixs[i]));
		}
		list.add(resourceNameToUse);
		return list;
	}

	protected String getEnvironmentalResourceName(String resourceName, String evnironmental) {
		int index = resourceName.lastIndexOf(".");
		if (index == -1) {// 不存在
			return resourceName + evnironmental;
		} else {
			return resourceName.substring(0, index) + evnironmental + resourceName.substring(index);
		}
	};

	/**
	 * 可使用的资源列表，使用优先级从左到右
	 * 
	 * @param resource
	 * @return
	 */
	public List<Resource> getResources(String resource) {
		List<String> nameList = getEnvironmentalResourceNameList(resource);
		List<Resource> resources = new ArrayList<Resource>(nameList.size());
		for (String name : nameList) {
			Resource res = isCacheEnable() ? getResourceByCache(name) : super.getResource(name);
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
				resourceCache.putIfAbsent(location, Resource.NONEXISTENT_RESOURCE);
			}
		}
		return resource;
	}

	@Override
	public Resource getResource(String location) {
		List<String> nameList = getEnvironmentalResourceNameList(location);
		for (String name : nameList) {
			Resource resource = isCacheEnable() ? getResourceByCache(name) : super.getResource(name);
			if (resource == null || !resource.exists()) {
				continue;
			}

			return resource;
		}

		return null;
	}

	public void loadProperties(Properties properties, Resource resource, String charsetName) {
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
					Method method = ReflectionUtils.getMethod(Properties.class, "load", Reader.class);
					if (method == null) {
						FormatUtils.warn(getClass(), "jdk1.6及以上的版本才支持指定字符集: {}" + resource.getDescription());
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

	public void formatProperties(Properties properties, PropertyFactory propertyFactory) {
		if (properties == null || propertyFactory == null) {
			return;
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			entry.setValue(propertyFactory.format(value.toString(), true));
		}
	}

	public ObservableResource<Properties> getProperties(String resource) {
		return getProperties(resource, null);
	}

	public ObservableResource<Properties> getProperties(final String resource, final String charsetName) {
		final List<Resource> resources = getResources(resource);
		if (CollectionUtils.isEmpty(resources)) {
			return new NonexistentObservableResource<Properties>();
		}

		Properties properties = new Properties();
		ListIterator<Resource> iterator = resources.listIterator(resources.size());
		while (iterator.hasPrevious()) {
			Resource res = iterator.previous();
			loadProperties(properties, res, charsetName);
		}

		return new ObservableResource<Properties>(properties) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<Properties> eventListener) {
				return resources.get(0).getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {

					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ObservableResourceEvent<Properties>(event,
								getProperties(resource, charsetName).getResource()));
					}
				});
			}
		};
	}

	public ObservableResource<String> getContent(final String resource, final String charsetName) {
		final Resource res = getResource(resource);
		String content = ResourceUtils.getContent(getResource(resource), charsetName);
		return new ObservableResource<String>(content) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<String> eventListener) {
				return res.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ObservableResourceEvent<String>(event,
								getContent(resource, charsetName).getResource()));
					}
				});
			}
		};
	}

	public ObservableResource<String> getContent(final String resource, final Charset charset) {
		final Resource res = getResource(resource);
		String content = ResourceUtils.getContent(getResource(resource), charset);
		return new ObservableResource<String>(content) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<String> eventListener) {
				return res.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(
								new ObservableResourceEvent<String>(event, getContent(resource, charset).getResource()));
					}
				});
			}
		};
	}

	public ObservableResource<List<String>> getLines(final String resource, final Charset charset) {
		final Resource res = getResource(resource);
		List<String> lines = ResourceUtils.getLines(getResource(resource), charset);
		return new ObservableResource<List<String>>(lines) {

			@Override
			public EventRegistration registerListener(
					final ObservableResourceEventListener<List<String>> eventListener) {
				return res.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ObservableResourceEvent<List<String>>(event,
								getLines(resource, charset).getResource()));
					}
				});
			}
		};
	}

	public ObservableResource<List<String>> getLines(final String resource, final String charsetName) {
		final Resource res = getResource(resource);
		List<String> lines = ResourceUtils.getLines(getResource(resource), charsetName);
		return new ObservableResource<List<String>>(lines) {

			@Override
			public EventRegistration registerListener(
					final ObservableResourceEventListener<List<String>> eventListener) {
				return res.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ObservableResourceEvent<List<String>>(event,
								getLines(resource, charsetName).getResource()));
					}
				});
			}
		};
	}

	public boolean isExist(String resource) {
		Resource res = getResource(resource);
		return res != null && res.exists();
	}

	public ObservableResource<Properties> getFormattedProperties(String resource, String charsetName,
			final PropertyFactory formatPropertyFactory) {
		final ObservableResource<Properties> res = getProperties(resource, charsetName);
		Properties properties = res.getResource();
		formatProperties(properties, formatPropertyFactory);
		return new ObservableResource<Properties>(properties) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<Properties> eventListener) {
				return res.registerListener(new ObservableResourceEventListener<Properties>() {

					public void onEvent(ObservableResourceEvent<Properties> event) {
						Properties properties = event.getSource();
						formatProperties(properties, formatPropertyFactory);
						eventListener.onEvent(new ObservableResourceEvent<Properties>(event, properties));
					}
				});
			}
		};
	}

	public ObservableResource<Properties> getFormattedProperties(String resource,
			PropertyFactory formatPropertyFactory) {
		return getFormattedProperties(resource, null, formatPropertyFactory);
	}

	public ObservableResource<Properties> getFormattedProperties(String resource, String charsetName) {
		return getFormattedProperties(resource, charsetName, getPropertyFactory());
	}

	public ObservableResource<Properties> getFormattedProperties(String resource) {
		return getFormattedProperties(resource, getPropertyFactory());
	}

	public ObservableResource<UnsafeByteArrayInputStream> getInputStream(String resource) {
		final ObservableResource<byte[]> res = getBytes(resource);
		return new ObservableResource<UnsafeByteArrayInputStream>(
				res.getResource() == null ? null : new UnsafeByteArrayInputStream(res.getResource())) {

			@Override
			public EventRegistration registerListener(
					final ObservableResourceEventListener<UnsafeByteArrayInputStream> eventListener) {
				return res.registerListener(new ObservableResourceEventListener<byte[]>() {

					public void onEvent(ObservableResourceEvent<byte[]> event) {
						eventListener.onEvent(new ObservableResourceEvent<UnsafeByteArrayInputStream>(event,
								res.getResource() == null ? null : new UnsafeByteArrayInputStream(res.getResource())));
					}
				});
			}
		};
	}

	public ObservableResource<byte[]> getBytes(final String resource) {
		final Resource res = getResource(resource);
		byte[] data = ResourceUtils.getBytes(res);
		return new ObservableResource<byte[]>(data) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<byte[]> eventListener) {
				return res.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {

					public void onEvent(ResourceEvent event) {
						eventListener
								.onEvent(new ObservableResourceEvent<byte[]>(event, getBytes(resource).getResource()));
					}
				});
			}
		};
	}
}
