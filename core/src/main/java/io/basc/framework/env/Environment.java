package io.basc.framework.env;

import io.basc.framework.util.element.Elements;

public interface Environment extends PropertyResolver {
	Elements<String> getActiveProfiles();

	Elements<String> getDefaultProfiles();

	Elements<String> getProfiles(String source);
}
