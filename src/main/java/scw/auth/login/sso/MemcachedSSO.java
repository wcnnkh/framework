package scw.auth.login.sso;

import scw.auth.login.MemcachedSessionFactory;
import scw.auth.login.Session;
import scw.common.utils.XUtils;
import scw.memcached.Memcached;

public class MemcachedSSO extends MemcachedSessionFactory implements SSO {

	public MemcachedSSO(Memcached memcached, String prefix, int exp) {
		super(memcached, prefix, exp);
	}

	@Override
	public Session login(String uid) {
		return login(uid + XUtils.getUUID(), uid);
	}

	@Override
	public void cancelLogin(String sessionId) {
		String uid = getMemcached().get(getPrefix() + sessionId);
		if (uid != null) {
			getMemcached().delete(getPrefix() + uid);
		}
		super.cancelLogin(sessionId);
	}

	public Session getSessionByUid(String uid) {
		String sid = getMemcached().get(getPrefix() + uid);
		if (sid == null) {
			return null;
		}
		return getSession(sid);
	}

	public void cancelLoginByUid(String uid) {
		String sid = getMemcached().get(getPrefix() + uid);
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
		String oldSid = getMemcached().get(getPrefix() + uid);
		if (oldSid != null) {
			getMemcached().delete(getPrefix() + oldSid);
		}
		getMemcached().set(getPrefix() + uid, sessionId);
		return super.login(sessionId, uid);
	}
}
