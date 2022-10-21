package io.basc.framework.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

public class DefaultProfilesResolver implements ProfilesResolver {
	private static final String[] PROFILES = new String[] { "io.basc.framework.env.profiles",
			"io_basc_framework_env_profiles" };

	/**
	 * 默认的实现
	 */
	public static final ProfilesResolver INSTANCE = new DefaultProfilesResolver();

	private String connector = "-";
	private boolean ignoreCase = true;

	public String getConnector() {
		return connector == null ? "" : connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	@Override
	public Collection<String> resolve(ValueFactory<String> factory, String name) {
		String[] profiles = getProfiles(factory);
		if (ArrayUtils.isEmpty(profiles)) {
			return Arrays.asList(name);
		}

		List<String> list = new ArrayList<String>(profiles.length + 1);
		String connector = getConnector();
		for (int i = profiles.length - 1; i >= 0; i--) {
			list.add(resolve(name, connector, profiles[i]));
		}
		list.add(name);
		return list;
	}

	protected String resolve(String name, String connector, String profile) {
		int index = name.lastIndexOf(".");
		if (index == -1) {// 不存在
			return name + connector + profile;
		} else {
			return name.substring(0, index) + connector + profile + name.substring(index);
		}
	};

	@Override
	public String[] getProfiles(ValueFactory<String> factory) {
		Value value = null;
		for (String key : PROFILES) {
			value = factory.get(key);
			if (isIgnoreCase() && (value == null || value.isEmpty())) {
				value = factory.get(key.toUpperCase());
			}

			if (value != null && !value.isEmpty()) {
				return value.getAsObject(String[].class);
			}
		}
		return StringUtils.EMPTY_ARRAY;
	}

}
