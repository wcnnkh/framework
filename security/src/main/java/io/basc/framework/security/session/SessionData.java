package io.basc.framework.security.session;

import io.basc.framework.util.attribute.SerializableAttributes;

public final class SessionData extends SerializableAttributes<String, Object> {
	private static final long serialVersionUID = 1L;
	private long createTime;
	private int maxInactiveInterval;
	private String sessionId;

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
