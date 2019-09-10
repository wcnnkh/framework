package scw.session;

import scw.beans.annotation.Bean;
import scw.data.memcached.Memcached;

@Bean(proxy=false)
public class MemcachedSessionFactory extends AbstractSessionFactory {
	private Memcached memcached;
	private String prefix;

	public MemcachedSessionFactory(int defaultMaxInactiveInterval, Memcached memcached, String prefix) {
		super(defaultMaxInactiveInterval);
		this.memcached = memcached;
		this.prefix = prefix;
	}

	private String getKey(String sessionId) {
		return prefix == null ? sessionId : (prefix + sessionId);
	}

	@Override
	public void setMaxInactiveInterval(String sessionId, int maxInactiveInterval) {
		memcached.touch(getKey(sessionId), maxInactiveInterval);
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return memcached.get(getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		memcached.set(getKey(sessionData.getSessionId()), sessionData.getMaxInactiveInterval(), sessionData);
	}

	@Override
	public void invalidate(String sessionId) {
		memcached.delete(getKey(sessionId));
	}

}
