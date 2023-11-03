package io.basc.framework.web;

import io.basc.framework.util.attribute.EditableAttributes;

public interface Session extends EditableAttributes<String, Object> {

	long getCreationTime();

	String getId();

	long getLastAccessedTime();

	void setMaxInactiveInterval(int maxInactiveInterval);

	int getMaxInactiveInterval();

	void invalidate();

	boolean isNew();
}
