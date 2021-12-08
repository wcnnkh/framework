package io.basc.framework.env;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.util.ArrayUtils;

public interface EnvironmentResourceLoader extends ResourcePatternResolver {

	/**
	 * 从可用的资源中选择一个可用的资源
	 * 
	 * @see #getResources(String)
	 */
	@Override
	default Resource getResource(String location) {
		Resource[] resources = getResources(location);
		if (ArrayUtils.isEmpty(resources)) {
			return null;
		}

		Resource resourceToUse = resources[resources.length - 1];
		for (Resource resource : resources) {
			if (resource.exists()) {
				resourceToUse = resource;
				break;
			}
		}
		return resourceToUse;
	}

	/**
	 * 资源是否存在
	 * 
	 * @see Resource#exists()
	 * @see #getResource(String)
	 * @param location
	 * @return
	 */
	default boolean exists(String location) {
		Resource resource = getResource(location);
		return resource != null && resource.exists();
	}

	/**
	 * 获取环境中可用的资源列表，可用优先级从高到低
	 * 
	 * @see ProfilesResolver#resolve(io.basc.framework.value.ValueFactory, String)
	 */
	Resource[] getResources(String locationPattern);
}
