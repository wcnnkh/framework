package io.basc.framework.env1;

import io.basc.framework.util.element.Elements;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
	void addActiveProfile(String profile);

	void setActiveProfiles(Elements<String> profiles);

	void setDefaultProfiles(Elements<String> profiles);
}
