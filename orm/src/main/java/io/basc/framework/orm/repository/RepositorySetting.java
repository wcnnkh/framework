package io.basc.framework.orm.repository;

import io.basc.framework.util.ThreadLocalMap;

public final class RepositorySetting {
	private static final ThreadLocalMap<String, String> LOCAL_RELATIONSHIP = new ThreadLocalMap<String, String>();
	private static final ThreadLocalMap<String, String> LOCAL_CONDITIONS = new ThreadLocalMap<String, String>();

	public static ThreadLocalMap<String, String> getLocalRelationship() {
		return LOCAL_RELATIONSHIP;
	}

	public static ThreadLocalMap<String, String> getLocalConditions() {
		return LOCAL_CONDITIONS;
	}
}