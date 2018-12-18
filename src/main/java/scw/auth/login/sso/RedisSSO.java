package scw.auth.login.sso;

import scw.auth.login.RedisSessionFactory;
import scw.auth.login.Session;
import scw.redis.Redis;

public class RedisSSO extends RedisSessionFactory implements SSO{
	public RedisSSO(Redis redis, String prefix, int exp) {
		super(redis, prefix, exp);
	}
	
	@Override
	public Session login(String uid) {
		String oldSid = getRedis().get(getPrefix() + uid);
		if(oldSid != null){
			getRedis().delete(getPrefix() + oldSid);
		}
		
		Session session = super.login(uid);
		getRedis().set(getPrefix() + session.getId(), uid);
		return session;
	}

	public Session getSessionByUid(String uid) {
		String sid = getRedis().get(getPrefix() + uid);
		if(sid == null){
			return null;
		}
		
		return getSession(sid);
	}
	
	@Override
	public void cancelLogin(String sessionId) {
		String uid = getRedis().get(getPrefix() + sessionId);
		if(uid != null){
			getRedis().delete(getPrefix() + uid);
		}
		super.cancelLogin(sessionId);
	}

	public void cancelLoginByUid(String uid) {
		String sid = getRedis().get(getPrefix() + uid);
		if(sid != null){
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
}
