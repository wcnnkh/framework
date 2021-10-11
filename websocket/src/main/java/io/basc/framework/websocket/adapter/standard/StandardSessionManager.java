package io.basc.framework.websocket.adapter.standard;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.websocket.Session;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.stream.Callback;

/**
 * 一个标准的session管理器
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class StandardSessionManager<T> {
	private static Logger logger = LoggerFactory.getLogger(StandardSessionManager.class);
	private final String groupKey;
	private final ConcurrentHashMap<T, ConcurrentHashMap<String, Session>> groupMap = new ConcurrentHashMap<>();

	public StandardSessionManager(String groupKey) {
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
			if (logger.isDebugEnabled()) {
				logger.debug("{}[{}][{}]插入标识成功", groupKey, group, session.getId());
			}
			return true;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("{}[{}][{}]插入标识失败", groupKey, group, session.getId());
		}
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
		if (sessionMap == null) {
			ConcurrentHashMap<String, Session> newSessionMap = new ConcurrentHashMap<>(4);
			sessionMap = groupMap.putIfAbsent(group, newSessionMap);
			if (sessionMap == null) {
				sessionMap = newSessionMap;
			}
		}

		if (sessionMap.putIfAbsent(session.getId(), safeSessionProxy) == null) {
			if (logger.isDebugEnabled()) {
				logger.info("{}[{}][{}]保存session成功", groupKey, group, session.getId());
			}
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

	/**
	 * 移除指定session
	 * @param group
	 * @param sessionId
	 * @return 如果移除成功返回移除的session，否则返回空
	 */
	@Nullable
	public Session remove(T group, String sessionId) {
		Assert.requiredArgument(group != null, "group");
		Assert.requiredArgument(sessionId != null, "sessionId");
		ConcurrentHashMap<String, Session> sessionMap = groupMap.get(group);
		if (sessionMap == null) {
			return null;
		}

		Session session = sessionMap.remove(sessionId);
		if (session != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("{}[{}][{}]移除session成功", groupKey, group, sessionId);
			}
		}
		return session;
	}

	/**
	 * 获取指定session
	 * @param group
	 * @param sessionId
	 * @return 如果存在返回session,否则返回空
	 */
	@Nullable
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
			if (logger.isDebugEnabled()) {
				logger.info("{}[{}][{}]移除session成功", groupKey, group,
						sessions.stream().map((session) -> session.getId()).collect(Collectors.toList()));
			}
		}
		return sessions;
	}
	
	public void remove(T group, Callback<Session, IOException> processor) {
		remove(group).stream().filter((s) -> s.isOpen()).forEach((session) -> process(group, session, processor));
	}
	
	public void remove(T group, Processor<T> processor) {
		remove(group).stream().filter((s) -> s.isOpen()).forEach((session) -> process(group, session, processor));
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
	
	public void process(T group, Session session, Callback<Session, IOException> processor) {	
		if(processor == null) {
			return ;
		}
		
		if(!session.isOpen()) {
			return ;
		}
		
		try {
			processor.call(session);
		} catch (IOException e) {
			logger.error(e, "Process group[{}] session[{}] info[{}]", group, session.getId(), session);
		}
	}
	
	public void process(T group, Session session, Processor<T> processor) {
		if(processor == null) {
			return ;
		}
		
		if(!session.isOpen()) {
			return ;
		}
		
		try {
			processor.process(group, session);
		} catch (IOException e) {
			logger.error(e, "Process group[{}] session[{}] info[{}]", group, session.getId(), session);
		}
	}
	
	public void forEach(T group, Callback<Session, IOException> processor) {
		getSessions(group).stream().filter((s) -> s.isOpen()).forEach((s) -> process(group, s, processor));
	}
	
	public void forEach(T group, Processor<T> processor) {
		getSessions(group).stream().filter((s) -> s.isOpen()).forEach((s) -> process(group, s, processor));
	}
	
	public void forEach(Callback<Session, IOException> processor) {
		getGroups().forEach((group) -> forEach(group, processor));
	}
	
	public void forEach(Processor<T> processor) {
		getGroups().forEach((group) -> forEach(group, processor));
	}
	
	public void clear(Callback<Session, IOException> processor) {
		getGroups().forEach((group) -> remove(group, processor));
	}
	
	public void clear(Processor<T> processor) {
		getGroups().forEach((group) -> remove(group, processor));
	}
	
	@FunctionalInterface
	public static interface Processor<T>{
		void process(T group, Session session) throws IOException;
	}
	
	/**
	 * 向指定的session发送
	 * @see Session#getBasicRemote()
	 * @param session
	 * @param text
	 * @throws IOException
	 */
	public void sendText(Session session, String text) throws IOException {
		session.getBasicRemote().sendText(text);
	}
	
	public void sendText(String text) {
		if(text == null) {
			return ;
		}
		
		forEach((s) -> sendText(s, text));
	}
	
	public void sendText(String text, Collection<? extends T> groups) {
		if(CollectionUtils.isEmpty(groups) || text == null) {
			return ;
		}
		
		groups.forEach((group) -> forEach(group, (s) -> sendText(s, text)));
	}
	
	public void sendText(String text, T group) {
		sendText(text, Arrays.asList(group));
	}
}
