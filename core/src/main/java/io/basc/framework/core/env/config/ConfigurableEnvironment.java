package io.basc.framework.core.env.config;

import io.basc.framework.core.env.Environment;
import io.basc.framework.util.collections.Elements;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
	void addActiveProfile(String profile);

	void setActiveProfiles(Elements<String> profiles);

	void setDefaultProfiles(Elements<String> profiles);

	MutablePropertySources getPropertySources();

	void setParentEnvironment(ConfigurableEnvironment parentEnvironment);
}
