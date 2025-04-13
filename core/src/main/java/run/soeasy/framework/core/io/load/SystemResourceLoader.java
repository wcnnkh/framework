package run.soeasy.framework.core.io.load;

import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.strings.StringUtils;

public final class SystemResourceLoader extends DefaultResourceLoader implements ResourceLoader {
	private static volatile SystemResourceLoader instance;

	public static SystemResourceLoader getInstance() {
		if (instance == null) {
			synchronized (SystemResourceLoader.class) {
				if (instance == null) {
					instance = new SystemResourceLoader();
					instance.configure();
				}
			}
		}
		return instance;
	}

	private SystemResourceLoader() {
		super();
	}

	/**
	 * 因为eclipse默认打包为可执行jar会将资源打包在resources目录下，所以会尝试在此目录下查找资源
	 * 
	 * @see #getResource(Class, String)
	 * @see #getResource(ClassLoader, String)
	 * @see #getResourceAsStream(Class, String)
	 * @see #getResourceAsStream(ClassLoader, String)
	 * @see #getResources(ClassLoader, String)
	 * @see #getClassLoaderResources(String)
	 * @see #getClassLoaderResource(String)
	 * @see #getClassLoaderResourceAsStream(String)
	 */
	private static final String[] RESOURCE_PREFIXS;

	static {
		String prefixs = System.getProperty("io.basc.framework.resource.prefixs");
		String[] resourcePrefixs = new String[] { "resources/" };
		if (StringUtils.isNotEmpty(prefixs)) {
			String[] array = StringUtils.splitToArray(prefixs);
			if (array != null && array.length != 0) {
				resourcePrefixs = ArrayUtils.merge(array, resourcePrefixs);
			}
		}

		for (int i = 0; i < resourcePrefixs.length; i++) {
			String prefix = resourcePrefixs[i];
			while (prefix.endsWith("/")) {
				prefix = prefix.substring(0, prefix.length() - 2);
			}

			if (prefix.length() > 0) {
				prefix = prefix + "/";
			}

			resourcePrefixs[i] = prefix;
		}
		RESOURCE_PREFIXS = resourcePrefixs;
	}

	@Override
	public Resource getResource(String location) {
		String pathToUse = StringUtils.cleanPath(location);
		for (String prefix : RESOURCE_PREFIXS) {
			if (pathToUse.startsWith(prefix)) {
				continue;
			}

			Resource resource = super.getResource(pathToUse);
			if (resource == null || resource.exists()) {
				return resource;
			}
		}
		return super.getResource(pathToUse);
	}
}
