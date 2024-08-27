package io.basc.framework.env;

import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public class DefaultEnvironment extends DefaultPropertyResolver implements ConfigurableEnvironment {
	private static final String ACTIVE_PROFILES_PROPERTY_NAME = System.getProperty("basc.profiles.active.property.name",
			"basc.profiles.active");
	private static final String DEFAULT_PROFILES_PROPERTY_NAME = System
			.getProperty("basc.profiles.default.property.name", "basc.profiles.default");
	private String connector = "-";

	private boolean ignoreCase = true;

	private Environment parentEnvironment;

	public DefaultEnvironment() {
		getFactories().setLastService(SystemProperties.getInstance());
	}

	@Override
	public void addActiveProfile(String profile) {
		Elements<String> activeProfiles = getActiveProfilesInProperties();
		activeProfiles = activeProfiles.concat(Elements.singleton(profile));
		String value = activeProfiles.collect(Collectors.joining(","));
		put(ACTIVE_PROFILES_PROPERTY_NAME, value);
	}

	@Override
	public Elements<String> getActiveProfiles() {
		return getDefaultProfiles().concat(getActiveProfilesInProperties()).distinct();
	}

	public Elements<String> getActiveProfilesInProperties() {
		String values = getAsString(ACTIVE_PROFILES_PROPERTY_NAME);
		if (StringUtils.isEmpty(values)) {
			return Elements.empty();
		}
		return StringUtils.split(values).map((e) -> e.getSource().toString());
	}

	public String getConnector() {
		return connector == null ? "" : connector;
	}

	@Override
	public Elements<String> getDefaultProfiles() {
		Elements<String> profiles = getDefaultProfilesInProperties();
		if (profiles.isEmpty() && parentEnvironment != null) {
			return parentEnvironment.getDefaultProfiles();
		}
		return profiles;
	}

	public Elements<String> getDefaultProfilesInProperties() {
		String values = getAsString(DEFAULT_PROFILES_PROPERTY_NAME);
		if (StringUtils.isEmpty(values)) {
			return Elements.empty();
		}
		return StringUtils.split(values).map((e) -> e.getSource().toString()).distinct();
	}

	public Environment getParentEnvironment() {
		return parentEnvironment;
	}

	@Override
	public Elements<String> getProfiles(String source) {
		Elements<String> profiles = getActiveProfiles();
		if (profiles.isEmpty()) {
			return Elements.singleton(source);
		}

		String connector = getConnector();
		return profiles.map((profile) -> {
			return resolve(source, connector, profile);
		});
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	protected String resolve(String name, String connector, String profile) {
		int index = name.lastIndexOf(".");
		if (index == -1) {// 不存在
			return name + connector + profile;
		} else {
			return name.substring(0, index) + connector + profile + name.substring(index);
		}
	}

	@Override
	public void setActiveProfiles(Elements<String> profiles) {
		String value = profiles == null ? "" : profiles.collect(Collectors.joining(","));
		put(ACTIVE_PROFILES_PROPERTY_NAME, value);
	}

	public void setConnector(String connector) {
		this.connector = connector;
	};

	@Override
	public void setDefaultProfiles(Elements<String> profiles) {
		String value = profiles == null ? "" : profiles.collect(Collectors.joining(","));
		put(DEFAULT_PROFILES_PROPERTY_NAME, value);
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public void setParentEnvironment(Environment parentEnvironment) {
		this.parentEnvironment = parentEnvironment;
		setParentPropertyResolver(parentEnvironment);
	}
}
