package io.basc.framework.env1.support;

import io.basc.framework.env1.ConfigurableEnvironmentResources;
import io.basc.framework.env1.EnvironmentResources;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.util.element.Elements;

public class DefaultEnvironmentResources implements ConfigurableEnvironmentResources {
	private final DefaultEnvironment environment = new DefaultEnvironment();
	private final ConfigurablePropertiesResolver propertiesResolver = new ConfigurablePropertiesResolver();
	private EnvironmentResources parentEnvironmentResources;
	private ResourceLoader resourceLoader;

	public EnvironmentResources getParentEnvironmentResources() {
		return parentEnvironmentResources;
	}

	public void setParentEnvironmentResources(EnvironmentResources parentEnvironmentResources) {
		this.parentEnvironmentResources = parentEnvironmentResources;
		environment.setParentEnvironment(
				parentEnvironmentResources == null ? null : parentEnvironmentResources.getEnvironment());
		propertiesResolver.setParentPropertiesResolver(
				parentEnvironmentResources == null ? null : parentEnvironmentResources.getPropertiesResolver());
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public DefaultEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public ConfigurablePropertiesResolver getPropertiesResolver() {
		return propertiesResolver;
	}

	@Override
	public Elements<Resource> getProfileResources(String location) {
		ResourceLoader resourceLoader = getResourceLoader();
		if (resourceLoader == null) {
			return Elements.empty();
		}

		Resource rootResource = resourceLoader.getResource(location);
		Elements<Resource> root = Elements.singleton(rootResource);
		Elements<String> profiles = getEnvironment().getProfiles(location);
		if (profiles.isEmpty()) {
			return root;
		}

		Elements<Resource> resourceProfiles = profiles.map((name) -> resourceLoader.getResource(name));
		return root.concat(resourceProfiles);
	}
}
