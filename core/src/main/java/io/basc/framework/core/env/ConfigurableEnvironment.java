package io.basc.framework.core.env;

import io.basc.framework.util.Elements;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
	void addActiveProfile(String profile);

	void setActiveProfiles(Elements<String> profiles);

	void setDefaultProfiles(Elements<String> profiles);

	MutablePropertySources getPropertySources();

	void setParentEnvironment(ConfigurableEnvironment parentEnvironment);
}
