package scw.session;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import scw.mvc.AttributeManager;

public final class SessionData implements AttributeManager, Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> attributeMap;
	private long createTime;
	private int maxInactiveInterval;
	private String sessionId;

	public Object getAttribute(String name) {
		return attributeMap == null ? null : attributeMap.get(name);
	}

	public void setAttribute(String name, Object value) {
		if (attributeMap == null) {
			attributeMap = new HashMap<String, Object>(4);
		}
		attributeMap.put(name, value);
	}

	public void removeAttribute(String name) {
		if (attributeMap == null) {
			return;
		}

		attributeMap.remove(name);
	}

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

	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		return (Enumeration<String>) (attributeMap == null ? Collections.emptyEnumeration()
				: Collections.enumeration(attributeMap.keySet()));
	}
}
