package run.soeasy.framework.http;

import run.soeasy.framework.util.attribute.EditableAttributes;

public interface HttpSession extends EditableAttributes<String, Object> {

	long getCreationTime();

	String getId();

	long getLastAccessedTime();

	void setMaxInactiveInterval(int maxInactiveInterval);

	int getMaxInactiveInterval();

	void invalidate();

	boolean isNew();
}
