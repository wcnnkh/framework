package scw.auth.login;

import scw.common.utils.StringUtils;
import scw.common.utils.XUtils;
import scw.redis.Redis;

public class RedisSessionFactory implements SessionFactory {
	private final Redis redis;
	private final String prefix;
	private final int exp;

	public RedisSessionFactory(Redis redis, String prefix, int exp) {
		this.redis = redis;
		this.prefix = prefix;
		this.exp = exp;
	}

	public Session getSession(String sessionId) {
		if (StringUtils.isNull(sessionId)) {
			return null;
		}

		String uid = redis.getAndTouch(prefix + sessionId, exp);
		if (uid == null) {
			return null;
		}

		return new Session(sessionId, uid);
	}

	public Session login(String uid) {
		return login(uid + XUtils.getUUID(), uid);
	}

	public void cancelLogin(String sessionId) {
		if (sessionId == null) {
			return;
		}

		redis.delete(prefix + sessionId);
	}

	public Redis getRedis() {
		return redis;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getExp() {
		return exp;
	}

	public Session login(long uid) {
		return login(uid + "");
	}

	public Session login(int uid) {
		return login(uid + "");
	}

	public Session login(String sessionId, String uid) {
		redis.setex(prefix + sessionId, exp, uid);
		return new Session(sessionId, uid);
	}
}
