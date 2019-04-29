package scw.utils.login;

import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.data.redis.Redis;

public class RedisLoginFactory implements LoginFactory {
	private final Redis redis;
	private final String prefix;
	private final int exp;

	public RedisLoginFactory(Redis redis, String prefix, int exp) {
		this.redis = redis;
		this.prefix = prefix;
		this.exp = exp;
	}

	public Session getSession(String sessionId) {
		if (StringUtils.isNull(sessionId)) {
			return null;
		}

		String uid = redis.getStringOperations().getAndTouch(prefix + sessionId, exp);
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

		redis.getStringOperations().del(prefix + sessionId);
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
		redis.getStringOperations().setex(prefix + sessionId, exp, uid);
		return new Session(sessionId, uid);
	}
}
