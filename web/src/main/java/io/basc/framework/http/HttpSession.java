package io.basc.framework.http;

import io.basc.framework.util.attribute.EditableAttributes;

public interface HttpSession extends EditableAttributes<String, Object> {

	long getCreationTime();

	String getId();

	long getLastAccessedTime();

	void setMaxInactiveInterval(int maxInactiveInterval);

	int getMaxInactiveInterval();

	void invalidate();

	boolean isNew();
}
