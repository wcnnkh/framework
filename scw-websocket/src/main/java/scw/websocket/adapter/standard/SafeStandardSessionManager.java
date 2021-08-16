package scw.websocket.adapter.standard;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.websocket.Session;

import scw.core.Assert;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

/**
 * 这是一个安全的session管理器
 * @author shuchaowen
 *
 * @param <T>
 */
public class SafeStandardSessionManager<T> {
	private static Logger logger = LoggerFactory.getLogger(SafeStandardSessionManager.class);
	private final String groupKey;
	private final ConcurrentHashMap<T, ConcurrentHashMap<String, Session>> groupMap = new ConcurrentHashMap<>();

	public SafeStandardSessionManager(String groupKey) {
		this.groupKey = groupKey;
	}

	public final String getGroupKey() {
		return groupKey;
	}

	@SuppressWarnings("unchecked")
	public T getGroup(Session session) {
		Assert.requiredArgument(session != null, "session");
		return (T) session.getUserProperties().get(groupKey);
	}

	/**
	 * 向session中插入group标识
	 * 
	 * @param session
	 * @param group
	 * @return 如果已存在groupKey就返回false
	 */
	public boolean setGroup(Session session, T group) {
		Assert.requiredArgument(session != null, "session");
		Assert.requiredArgument(group != null, "group");
		if (session.getUserProperties().putIfAbsent(groupKey, group) == null) {
			logger.debug("{}[{}][{}]插入标识成功", groupKey, group, session.getId());
			return true;
		}
		logger.error("{}[{}][{}]插入标识失败", groupKey, group, session.getId());
		return false;
	}

	/**
	 * 保存session到指定的分组
	 * 
	 * @param group
	 * @param session
	 * @return 如果插入成功就返回成功的session
	 */
	public Session putIfAbsent(T group, Session session) {
		Assert.requiredArgument(group != null, "group");
		Assert.requiredArgument(session != null, "session");
		SafeSessionProxy safeSessionProxy = SafeSessionProxy.proxy(session);
		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if(sessionMap == null) {
			ConcurrentHashMap<String, Session> newSessionMap = new ConcurrentHashMap<>(4);
			sessionMap = groupMap.putIfAbsent(group, newSessionMap);
			if(sessionMap == null) {
				sessionMap = newSessionMap;
			}
		}
		
		if(sessionMap.putIfAbsent(session.getId(), safeSessionProxy) == null) {
			logger.info("{}[{}][{}]保存session成功", groupKey, group, session.getId());
			return safeSessionProxy;
		}
		
		return null;
	}

	public Session register(T group, Session session) {
		Assert.requiredArgument(group != null, "group");
		Assert.requiredArgument(session != null, "session");
		if (setGroup(session, group)) {
			return putIfAbsent(group, session);
		}
		return null;
	}

	public boolean remove(Session session) {
		Assert.requiredArgument(session != null, "session");
		T group = getGroup(session);
		if (group == null) {
			return false;
		}

		return remove(group, session.getId()) != null;
	}

	public Session remove(T group, String sessionId) {
		Assert.requiredArgument(group != null, "group");
		Assert.requiredArgument(sessionId != null, "sessionId");
		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if (sessionMap == null) {
			return null;
		}

		Session session = sessionMap.remove(sessionId);
		if (session != null) {
			logger.info("{}[{}][{}]移除session成功", groupKey, group, sessionId);
		}
		return session;
	}

	public Session getSession(T group, String sessionId) {
		Assert.requiredArgument(group != null, "group");
		Assert.requiredArgument(sessionId != null, "sessionId");
		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if (sessionMap == null) {
			return null;
		}
		return sessionMap.get(sessionId);
	}

	public boolean exists(T group) {
		Assert.requiredArgument(group != null, "group");
		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if (sessionMap == null) {
			return false;
		}

		if (sessionMap.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 只要分组中有一个在线就视为在线
	 * 
	 * @param group
	 * @return
	 */
	public boolean isOnline(T group) {
		Assert.requiredArgument(group != null, "group");
		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if (sessionMap == null) {
			return false;
		}

		if (sessionMap.isEmpty()) {
			return false;
		}

		return sessionMap.values().stream().filter((session) -> session.isOpen()).findFirst().isPresent();
	}

	public boolean isOnline(T group, String sessionId) {
		Assert.requiredArgument(group != null, "group");
		Assert.requiredArgument(sessionId != null, "sessionId");
		Session session = getSession(group, sessionId);
		if (session == null) {
			return false;
		}
		return session.isOpen();
	}

	public boolean exists(Session session) {
		Assert.requiredArgument(session != null, "session");
		T group = getGroup(session);
		if (group == null) {
			return false;
		}

		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if (sessionMap == null) {
			return false;
		}
		return sessionMap.contains(session.getId());
	}

	public Collection<Session> getSessions(T group) {
		Assert.requiredArgument(group != null, "group");
		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if (sessionMap == null) {
			return Collections.emptyList();
		}

		return sessionMap.values();
	}

	public Collection<T> getGroups() {
		return Collections.list(groupMap.keys());
	}

	public Collection<Session> remove(T group) {
		Assert.requiredArgument(group != null, "group");
		Map<String, Session> sessionMap = groupMap.remove(group);
		if (sessionMap == null) {
			return Collections.emptyList();
		}

		Collection<Session> sessions = sessionMap.values();
		if (!sessions.isEmpty() && logger.isDebugEnabled()) {
			logger.info("{}[{}][{}]移除session成功", groupKey, group,
					sessions.stream().map((session) -> session.getId()).collect(Collectors.toList()));
		}
		return sessions.stream().filter((session) -> session.isOpen()).collect(Collectors.toList());
	}

	public int getGroupSize() {
		return groupMap.size();
	}

	public long getSessionCount() {
		long size = 0;
		for (Entry<T, ConcurrentHashMap<String, Session>> entry : groupMap.entrySet()) {
			size += entry.getValue().values().stream().filter((session) -> session.isOpen()).count();
		}
		return size;
	}
}
