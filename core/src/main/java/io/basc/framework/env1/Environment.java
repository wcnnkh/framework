package io.basc.framework.env1;

import io.basc.framework.util.element.Elements;

public interface Environment extends PropertyResolver {
	Elements<String> getActiveProfiles();

	Elements<String> getDefaultProfiles();

	Elements<String> getProfiles(String source);
}
