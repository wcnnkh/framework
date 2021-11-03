package io.basc.framework.security.session;

import java.util.HashSet;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.Order;
import io.basc.framework.data.TemporaryStorage;
import io.basc.framework.data.memory.MemoryDataOperations;
import io.basc.framework.env.Sys;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultUserSessionFactory implements UserSessionFactory {
	private static final String USER_SESSION_PREFIX = Sys.env.getValue(
			UserSessionFactory.class.getPackage().getName() + ".prefix", String.class,
			StringUtils.replace(UserSessionFactory.class.getPackage().getName(), '.', ':') + ":user:");

	private static Logger logger = LoggerFactory.getLogger(DefaultUserSessionFactory.class);
	private final TemporaryStorage temporaryCache;
	private final SessionFactory sessionFactory;
	private String prefix = USER_SESSION_PREFIX;

	public DefaultUserSessionFactory() {
		this(new MemoryDataOperations());
		logger.info("Using memory {}", getTemporaryCache());
	}

	public DefaultUserSessionFactory(TemporaryStorage temporaryCache) {
		this(86400 * 7, temporaryCache);
	}

	@Order
	public DefaultUserSessionFactory(int maxInactiveInterval, TemporaryStorage temporaryCache) {
		this(temporaryCache, new DefaultSessionFactory(maxInactiveInterval, temporaryCache));
	}

	public DefaultUserSessionFactory(TemporaryStorage temporaryCache, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.temporaryCache = temporaryCache;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		this.prefix = prefix;
	}

	public TemporaryStorage getTemporaryCache() {
		return temporaryCache;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public <T> UserSessions<T> getUserSessions(T uid) {
		DefaultUserSessions<T> userSessions = new DefaultUserSessions<T>(uid);
		SessionIds<T> sessionIds = getSessionIds(uid, false);
		if (sessionIds != null) {
			for (String id : sessionIds) {
				Session session = getUserSession(uid, id);
				if (session == null) {
					continue;
				}

				userSessions.add(session);
			}
		}
		return userSessions;
	}

	private <T> String getUidToSessionIdsKey(T uid) {
		return prefix + uid;
	}

	public <T> SessionIds<T> getSessionIds(T uid, boolean create) {
		String key = getUidToSessionIdsKey(uid);
		SessionIds<T> ids = temporaryCache.getAndTouch(key, sessionFactory.getMaxInactiveInterval());
		if (ids == null) {
			if (create) {
				while (true) {
					ids = temporaryCache.getAndTouch(key, sessionFactory.getMaxInactiveInterval());
					if (ids != null) {
						break;
					}

					ids = new SessionIds<T>(sessionFactory.getMaxInactiveInterval());
					if (temporaryCache.add(key, sessionFactory.getMaxInactiveInterval(), ids)) {
						break;
					}
				}
			}
		}
		return ids;
	}

	public <T> void updateSessionIds(T uid, SessionIds<T> sessionIds) {
		String key = getUidToSessionIdsKey(uid);
		temporaryCache.set(key, sessionIds.getMaxInactiveInterval(), sessionIds);
	}

	protected <T> void createUserSession(T uid, String sessionId) {
		String key = getUidToSessionIdsKey(uid);
		SessionIds<T> ids = temporaryCache.get(key);
		if (ids == null) {
			ids = new SessionIds<T>(sessionFactory.getMaxInactiveInterval());
			ids.add(sessionId);
			if (!temporaryCache.add(key, sessionFactory.getMaxInactiveInterval(), ids)) {
				createUserSession(uid, sessionId);
			}
		} else {
			ids.add(sessionId);
			temporaryCache.set(key, ids);
		}
	}

	public <T> UserSession<T> getUserSession(T uid, String sessionId, boolean create) {
		Session session = sessionFactory.getSession(uid + ":" + sessionId, create);
		if (session == null) {
			return null;
		}

		if (session.isNew()) {
			SessionIds<T> sessionIds = getSessionIds(uid, true);
			sessionIds.add(sessionId);
			updateSessionIds(uid, sessionIds);
		}
		return new InternalUserSession<T>(uid, sessionId, session);
	}

	private static final class SessionIds<T> extends HashSet<String> {
		private static final long serialVersionUID = 1L;
		private int maxInactiveInterval;

		public SessionIds(int maxInactiveInterval) {
			this.maxInactiveInterval = maxInactiveInterval;
		}

		public int getMaxInactiveInterval() {
			return maxInactiveInterval;
		}

		public void setMaxInactiveInterval(int maxInactiveInterval) {
			this.maxInactiveInterval = maxInactiveInterval;
		}
	}

	private final class InternalUserSession<T> extends SessionWrapper implements UserSession<T> {
		private final String dispalySessionId;
		private final T uid;

		public InternalUserSession(T uid, String dispalySessionId, Session session) {
			super(session);
			this.dispalySessionId = dispalySessionId;
			this.uid = uid;
		}

		@Override
		public String getId() {
			return dispalySessionId;
		}

		@Override
		public void setMaxInactiveInterval(int maxInactiveInterval) {
			SessionIds<T> sessionIds = getSessionIds(uid, false);
			if (sessionIds != null && maxInactiveInterval > sessionIds.getMaxInactiveInterval()) {
				sessionIds.setMaxInactiveInterval(maxInactiveInterval);
				updateSessionIds(uid, sessionIds);
			}
			super.setMaxInactiveInterval(maxInactiveInterval);
		}

		@Override
		public void invalidate() {
			SessionIds<T> sessionIds = getSessionIds(uid, false);
			if (sessionIds != null) {
				sessionIds.remove(getId());
				updateSessionIds(uid, sessionIds);
			}
			super.invalidate();
		}

		public T getUid() {
			return uid;
		}
	}
}
