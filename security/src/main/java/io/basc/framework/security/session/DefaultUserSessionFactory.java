package io.basc.framework.security.session;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.Order;
import io.basc.framework.data.TemporaryStorageOperations;
import io.basc.framework.data.memory.MemoryOperations;
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
	private final TemporaryStorageOperations storageOperations;
	private final SessionFactory sessionFactory; 
	private String prefix = USER_SESSION_PREFIX;

	public DefaultUserSessionFactory() {
		this(new MemoryOperations());
		logger.info("Using memory {}", getSessionFactory());
	}

	public DefaultUserSessionFactory(TemporaryStorageOperations storageOperations) {
		this(86400 * 7, storageOperations);
	}

	@Order
	public DefaultUserSessionFactory(int maxInactiveInterval, TemporaryStorageOperations storageOperations) {
		this(storageOperations, new DefaultSessionFactory(maxInactiveInterval, storageOperations));
	}

	public DefaultUserSessionFactory(TemporaryStorageOperations storageOperations, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.storageOperations = storageOperations;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		this.prefix = prefix;
	}

	public TemporaryStorageOperations getStorageOperations() {
		return storageOperations;
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
		TypeDescriptor typeDescriptor = TypeDescriptor
				.valueOf(ResolvableType.forClassWithGenerics(SessionIds.class, uid.getClass()));
		SessionIds<T> ids = storageOperations.getAndTouch(typeDescriptor, key, sessionFactory.getMaxInactiveInterval(),
				TimeUnit.SECONDS);
		if (ids == null) {
			if (create) {
				while (true) {
					ids = storageOperations.getAndTouch(typeDescriptor, key, sessionFactory.getMaxInactiveInterval(),
							TimeUnit.SECONDS);
					if (ids != null) {
						break;
					}

					ids = new SessionIds<T>(sessionFactory.getMaxInactiveInterval());
					if (storageOperations.setIfAbsent(key, ids, sessionFactory.getMaxInactiveInterval(),
							TimeUnit.SECONDS)) {
						break;
					}
				}
			}
		}
		return ids;
	}

	public <T> void updateSessionIds(T uid, SessionIds<T> sessionIds) {
		String key = getUidToSessionIdsKey(uid);
		storageOperations.set(key, sessionIds, sessionIds.getMaxInactiveInterval(), TimeUnit.SECONDS);
	}

	protected <T> void createUserSession(T uid, String sessionId) {
		String key = getUidToSessionIdsKey(uid);
		TypeDescriptor typeDescriptor = TypeDescriptor
				.valueOf(ResolvableType.forClassWithGenerics(SessionIds.class, uid.getClass()));
		SessionIds<T> ids = storageOperations.get(typeDescriptor, key);
		if (ids == null) {
			ids = new SessionIds<T>(sessionFactory.getMaxInactiveInterval());
			ids.add(sessionId);
			if (!storageOperations.setIfAbsent(key, ids, sessionFactory.getMaxInactiveInterval(), TimeUnit.SECONDS)) {
				createUserSession(uid, sessionId);
			}
		} else {
			ids.add(sessionId);
			storageOperations.set(key, ids);
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
