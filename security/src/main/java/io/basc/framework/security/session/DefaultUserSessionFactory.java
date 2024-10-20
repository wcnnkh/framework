package io.basc.framework.security.session;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.data.TemporaryDataOperations;
import io.basc.framework.data.memory.MemoryOperations;
import io.basc.framework.env.Sys;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultUserSessionFactory implements UserSessionFactory {
	private static final String USER_SESSION_PREFIX = Sys.getEnv().getProperties()
			.get(UserSessionFactory.class.getPackage().getName() + ".prefix")
			.or(StringUtils.replace(UserSessionFactory.class.getPackage().getName(), '.', ':') + ":user:")
			.getAsString();

	private static Logger logger = LogManager.getLogger(DefaultUserSessionFactory.class);
	private final TemporaryDataOperations dataOperations;
	private final SessionFactory sessionFactory;
	private String prefix = USER_SESSION_PREFIX;

	public DefaultUserSessionFactory() {
		this(new MemoryOperations());
		logger.info("Using memory {}", getSessionFactory());
	}

	public DefaultUserSessionFactory(TemporaryDataOperations dataOperations) {
		this(86400 * 7, dataOperations);
	}

	public DefaultUserSessionFactory(int maxInactiveInterval, TemporaryDataOperations dataOperations) {
		this(dataOperations, new DefaultSessionFactory(maxInactiveInterval, dataOperations));
	}

	public DefaultUserSessionFactory(TemporaryDataOperations dataOperations, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.dataOperations = dataOperations;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		Assert.requiredArgument(prefix != null, "prefix");
		this.prefix = prefix;
	}

	public final TemporaryDataOperations getDataOperations() {
		return dataOperations;
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
		SessionIds<T> ids = dataOperations.getAndTouch(typeDescriptor, key, sessionFactory.getMaxInactiveInterval(),
				TimeUnit.SECONDS);
		if (ids == null) {
			if (create) {
				while (true) {
					ids = dataOperations.getAndTouch(typeDescriptor, key, sessionFactory.getMaxInactiveInterval(),
							TimeUnit.SECONDS);
					if (ids != null) {
						break;
					}

					ids = new SessionIds<T>(sessionFactory.getMaxInactiveInterval());
					if (dataOperations.setIfAbsent(key, ids, sessionFactory.getMaxInactiveInterval(),
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
		dataOperations.set(key, sessionIds, sessionIds.getMaxInactiveInterval(), TimeUnit.SECONDS);
	}

	protected <T> void createUserSession(T uid, String sessionId) {
		String key = getUidToSessionIdsKey(uid);
		TypeDescriptor typeDescriptor = TypeDescriptor
				.valueOf(ResolvableType.forClassWithGenerics(SessionIds.class, uid.getClass()));
		SessionIds<T> ids = dataOperations.get(typeDescriptor, key);
		if (ids == null) {
			ids = new SessionIds<T>(sessionFactory.getMaxInactiveInterval());
			ids.add(sessionId);
			if (!dataOperations.setIfAbsent(key, ids, sessionFactory.getMaxInactiveInterval(), TimeUnit.SECONDS)) {
				createUserSession(uid, sessionId);
			}
		} else {
			ids.add(sessionId);
			dataOperations.set(key, ids);
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
