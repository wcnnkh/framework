package scw.utils.login.sso;

import scw.common.utils.XUtils;
import scw.redis.Redis;
import scw.utils.login.RedisLoginFactory;
import scw.utils.login.Session;

public final class RedisSSO extends RedisLoginFactory implements SSO {
	public RedisSSO(Redis redis, String prefix, int exp) {
		super(redis, prefix, exp);
	}

	@Override
	public Session login(String uid) {
		return login(uid + XUtils.getUUID(), uid);
	}

	public Session getSessionByUid(String uid) {
		String sid = getRedis().get(getPrefix() + uid);
		if (sid == null) {
			return null;
		}

		return getSession(sid);
	}

	@Override
	public void cancelLogin(String sessionId) {
		String uid = getRedis().get(getPrefix() + sessionId);
		if (uid != null) {
			getRedis().delete(getPrefix() + uid);
		}
		super.cancelLogin(sessionId);
	}

	public void cancelLoginByUid(String uid) {
		String sid = getRedis().get(getPrefix() + uid);
		if (sid != null) {
			cancelLogin(sid);
		}
	}

	public Session getSessionByUid(long uid) {
		return getSessionByUid(uid + "");
	}

	public Session getSessionByUid(int uid) {
		return getSessionByUid(uid + "");
	}

	public void cancelLoginByUid(long uid) {
		cancelLoginByUid(uid + "");
	}

	public void cancelLoginByUid(int uid) {
		cancelLoginByUid(uid + "");
	}

	@Override
	public Session login(String sessionId, String uid) {
		String oldSid = getRedis().get(getPrefix() + uid);
		if (oldSid != null) {
			getRedis().delete(getPrefix() + oldSid);
		}
		getRedis().set(getPrefix() + uid, sessionId);
		return super.login(sessionId, uid);
	}
}
