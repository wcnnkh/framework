package io.basc.framework.security.session;

import io.basc.framework.util.attribute.EditableAttributesWrapper;

public final class DefaultSession extends EditableAttributesWrapper<SessionData, String, Object> implements Session {
	private AbstractSessionFactory sessionFactory;
	private boolean create;
	private long lastAccessedTime;

	public DefaultSession(AbstractSessionFactory sessionFactory, SessionData sessionData, boolean create) {
		super(sessionData);
		this.sessionFactory = sessionFactory;
		this.create = create;
		this.lastAccessedTime = System.currentTimeMillis();
	}

	public long getCreationTime() {
		return wrappedTarget.getCreateTime();
	}

	public String getId() {
		return wrappedTarget.getSessionId();
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		wrappedTarget.setMaxInactiveInterval(maxInactiveInterval);
		sessionFactory.setSessionData(wrappedTarget);
	}

	public int getMaxInactiveInterval() {
		return wrappedTarget.getMaxInactiveInterval();
	}

	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);
		sessionFactory.setSessionData(wrappedTarget);
	}

	public void removeAttribute(String name) {
		super.removeAttribute(name);
		sessionFactory.setSessionData(wrappedTarget);
	}

	public void invalidate() {
		sessionFactory.invalidate(wrappedTarget.getSessionId());
	}

	public boolean isNew() {
		return create;
	}

}
