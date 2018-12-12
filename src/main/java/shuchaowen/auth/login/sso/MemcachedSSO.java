package shuchaowen.auth.login.sso;

import shuchaowen.auth.login.MemcachedSessionFactory;
import shuchaowen.auth.login.Session;
import shuchaowen.memcached.Memcached;

public class MemcachedSSO extends MemcachedSessionFactory implements SSO{
	
	public MemcachedSSO(Memcached memcached, String prefix, int exp){
		super(memcached, prefix, exp);
	}
	
	@Override
	public Session login(String uid) {
		String oldSid = getMemcached().get(getPrefix() + uid);
		if(oldSid != null){
			getMemcached().delete(getPrefix() + oldSid);
		}
		
		Session session = super.login(uid);
		getMemcached().set(getPrefix() + uid, session.getId());
		return session;
	}
	
	@Override
	public void cancelLogin(String sessionId) {
		Long uid = getMemcached().get(getPrefix() + sessionId);
		if(uid != null){
			getMemcached().delete(getPrefix() + uid);
		}
		super.cancelLogin(sessionId);
	}

	public Session getSessionByUid(String uid) {
		String sid = getMemcached().get(getPrefix() + uid);
		if(sid == null){
			return null;
		}
		return getSession(sid);
	}

	public void cancelLoginByUid(String uid) {
		String sid = getMemcached().get(getPrefix() + uid);
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
