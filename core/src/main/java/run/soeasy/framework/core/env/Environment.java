package run.soeasy.framework.core.env;

import run.soeasy.framework.util.collections.Elements;

public interface Environment extends PropertyResolver {
	Elements<String> getActiveProfiles();

	Elements<String> getDefaultProfiles();

	Elements<String> getProfiles(String source);
}
