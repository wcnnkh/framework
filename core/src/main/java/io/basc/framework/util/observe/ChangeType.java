package io.basc.framework.util.observe;

public enum ChangeType {
	CREATE, DELETE, UPDATE;

	public static ChangeType getChangeType(Object before, Object after) {
		if (before == null && after != null) {
			return ChangeType.CREATE;
		} else if (before != null && after == null) {
			return ChangeType.DELETE;
		} else {
			return ChangeType.UPDATE;
		}
	}
}
