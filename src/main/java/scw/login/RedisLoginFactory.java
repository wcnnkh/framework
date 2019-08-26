package scw.login;

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

	public UserSessionMetaData getSession(String sessionId) {
		if (StringUtils.isNull(sessionId)) {
			return null;
		}

		String uid = redis.getStringOperations().getAndTouch(prefix + sessionId, exp);
		if (uid == null) {
			return null;
		}

		return new UserSessionMetaData(sessionId, uid);
	}

	public UserSessionMetaData login(String uid) {
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

	public UserSessionMetaData login(long uid) {
		return login(uid + "");
	}

	public UserSessionMetaData login(int uid) {
		return login(uid + "");
	}

	public UserSessionMetaData login(String sessionId, String uid) {
		redis.getStringOperations().setex(prefix + sessionId, exp, uid);
		return new UserSessionMetaData(sessionId, uid);
	}
}
