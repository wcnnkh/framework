package io.basc.framework.core.env;

import io.basc.framework.util.collections.Elements;

public interface Environment extends PropertyResolver {
	Elements<String> getActiveProfiles();

	Elements<String> getDefaultProfiles();

	Elements<String> getProfiles(String source);
}
