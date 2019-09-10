package scw.session.user;

import scw.session.Session;
import scw.session.SessionFactory;

public abstract class AbstractUserSessionFactory<T> implements UserSessionFactory<T> {
	private static final String PREFIX = "user_sid:";
	private String prefix;

	public AbstractUserSessionFactory(String prefix) {
		this.prefix = prefix;
	}

	protected String getKey(String key) {
		return prefix == null ? (PREFIX + key) : (PREFIX + prefix + key);
	}

	public abstract SessionFactory getSessionFactory();

	public Session getSession(String sessionId) {
		Session session = getSessionFactory().getSession(sessionId);
		if (session != null) {
			touch(getKey(sessionId), session.getMaxInactiveInterval());
		}
		return session;
	}

	@SuppressWarnings("unchecked")
	public T getUid(String sessionId) {
		Session session = getSessionFactory().getSession(sessionId);
		if (session == null) {
			return null;
		}

		return (T) getAndTouch(getKey(sessionId), session.getMaxInactiveInterval());
	}

	public abstract void touch(String key, int maxInactiveInterval);

	public abstract void set(String key, int maxInactiveInterval, Object value);

	public abstract Object get(String key);

	public abstract Object getAndTouch(String key, int maxInactiveInterval);

	public Session getSession(T uid, String sessionId, boolean create) {
		Session session = getSessionFactory().getSession(sessionId, create);
		if (session == null) {
			return null;
		}

		if (create) {
			set(getKey(sessionId), session.getMaxInactiveInterval(), uid);
		} else {
			touch(getKey(sessionId), session.getMaxInactiveInterval());
		}
		return session;
	}
}
