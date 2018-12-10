package shuchaowen.auth.login.sso;

import shuchaowen.auth.login.RedisSessionFactory;
import shuchaowen.auth.login.Session;
import shuchaowen.redis.Redis;

public class RedisSSO extends RedisSessionFactory implements SSO{

	public RedisSSO(Redis redis, String prefix, int exp) {
		super(redis, prefix, exp);
	}
	
	@Override
	public Session login(long uid) {
		String oldSid = getRedis().get(getPrefix() + uid);
		if(oldSid != null){
			getRedis().delete(getPrefix() + oldSid);
		}
		
		Session session = super.login(uid);
		getRedis().set(getPrefix() + session.getId(), uid + "");
		return session;
	}

	public Session getSession(long uid) {
		String sid = getRedis().get(getPrefix() + uid);
		if(sid == null){
			return null;
		}
		
		return getSession(sid);
	}
	
	@Override
	public void cancelLogin(String sessionId) {
		String uidStr = getRedis().get(getPrefix() + sessionId);
		if(uidStr != null){
			getRedis().delete(getPrefix() + uidStr);
		}
		super.cancelLogin(sessionId);
	}

	public void cancelLogin(long uid) {
		String sid = getRedis().get(getPrefix() + uid);
		if(sid != null){
			cancelLogin(sid);
		}
	}

}
