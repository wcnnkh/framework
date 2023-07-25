package io.basc.framework.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.event.observe.Observable;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.FileSystemResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.Resources;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.collect.ConcurrentReferenceHashMap;

class DefaultEnvironmentResourceLoader extends FileSystemResourceLoader
		implements ConfigurableEnvironmentResourceLoader {
	/**
	 * @see Resources
	 */
	private static final String AUTO_SELECT_RESOURCE = "basc.env.auto.select.resource";
	private static Logger logger = LoggerFactory.getLogger(DefaultEnvironmentResourceLoader.class);
	private final ConcurrentReferenceHashMap<String, Resource> cacheMap = new ConcurrentReferenceHashMap<String, Resource>(
			256);
	private ProfilesResolver profilesResolver = DefaultProfilesResolver.INSTANCE;
	private final Environment environment;
	private volatile Observable<Boolean> autoSelect;

	public DefaultEnvironmentResourceLoader(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		serviceLoaderFactory.getServiceLoader(ProfilesResolver.class).getServices().findFirst()
				.ifPresent(this::setProfilesResolver);
		super.configure(serviceLoaderFactory);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public ProfilesResolver getProfilesResolver() {
		return profilesResolver;
	}

	public void setProfilesResolver(ProfilesResolver profilesResolver) {
		Assert.requiredArgument(profilesResolver != null, "profilesResolver");
		logger.info("Set profiles resolver [{}]", profilesResolver);
		this.profilesResolver = profilesResolver;
	}

	@Override
	protected boolean ignoreClassPathResource(FileSystemResource resource) {
		return super.ignoreClassPathResource(resource) || resource.getPath().startsWith(environment.getWorkPath());
	}

	public boolean isAutoSelectResource() {
		if (autoSelect == null) {
			synchronized (this) {
				if (autoSelect == null) {
					autoSelect = environment.getProperties().getObservable(AUTO_SELECT_RESOURCE)
							.map((e) -> e.getAsBoolean());
				}
			}
		}
		return autoSelect.get();
	}

	@Override
	public Resource getResource(String location) {
		if (isAutoSelectResource()) {
			Resource[] resources = getResources(location);
			if (resources == null) {
				return ResourceUtils.NONEXISTENT_RESOURCE;
			}

			Resources rs = new Resources();
			rs.registerResources(Arrays.asList(resources));
			return rs;
		}
		return super.getResource(location);
	}

	@Override
	public Resource[] getResources(String locationPattern) {
		Collection<String> names = profilesResolver.resolve(environment.getProperties(),
				environment.getProperties().replacePlaceholders(locationPattern));
		List<Resource> resources = new ArrayList<Resource>(names.size());
		for (String name : names) {
			Resource res = getResourceByCache(name);
			if (res == null) {
				continue;
			}
			resources.add(res);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Get resources [{}] results {}", resources);
		}
		return resources.isEmpty() ? new Resource[0] : resources.toArray(new Resource[0]);
	}

	private Resource getResourceByCache(String location) {
		Resource resource = cacheMap.get(location);
		if (resource == null) {
			resource = super.getResource(location);
			if (resource == null) {
				return null;
			}

			// 不存在的资源不缓存
			if (resource.exists() && !resource.getName().endsWith(".class")) {
				Resource cache = cacheMap.putIfAbsent(location, resource);
				if (cache != null) {
					resource = cache;
				} else {
					// 出现一个新的资源时主动清理一下缓存
					cacheMap.purgeUnreferencedEntries();
					if (logger.isDebugEnabled()) {
						logger.debug("Find resource {} result {}", location, resource);
					}
				}
				return resource;
			}
			return ResourceUtils.NONEXISTENT_RESOURCE;
		}
		return resource;
	}
}
