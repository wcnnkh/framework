package scw.session;

import scw.beans.annotation.Bean;
import scw.data.redis.Redis;

@Bean(proxy=false)
public class RedisSessionFactory extends AbstractSessionFactory {
	private String prefix;
	private Redis redis;

	public RedisSessionFactory(int defaultMaxInactiveInterval, Redis redis, String prefix) {
		super(defaultMaxInactiveInterval);
		this.prefix = prefix;
		this.redis = redis;
	}

	private String getKey(String sessionId) {
		return prefix == null ? sessionId : (prefix + sessionId);
	}

	@Override
	public void setMaxInactiveInterval(String sessionId, int maxInactiveInterval) {
		redis.getObjectOperations().getAndTouch(sessionId, maxInactiveInterval);
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return (SessionData) redis.getObjectOperations().get(getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		redis.getObjectOperations().setex(getKey(sessionData.getSessionId()), sessionData.getMaxInactiveInterval(),
				sessionData);
	}

	@Override
	public void invalidate(String sessionId) {
		redis.getObjectOperations().del(getKey(sessionId));
	}

}
