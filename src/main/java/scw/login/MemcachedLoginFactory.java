package scw.login;

import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.data.memcached.Memcached;

public class MemcachedLoginFactory implements LoginFactory {
	private final Memcached memcached;
	private final String prefix;
	private final int exp;

	public MemcachedLoginFactory(Memcached memcached, String prefix, int exp) {
		this.memcached = memcached;
		this.prefix = prefix;
		this.exp = exp;
	}

	public UserSessionMetaData getSession(String sessionId) {
		if (StringUtils.isNull(sessionId)) {
			return null;
		}

		String uid = (String) memcached.getAndTouch(prefix + sessionId, exp);
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

		memcached.delete(prefix + sessionId);
	}

	public Memcached getMemcached() {
		return memcached;
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
		memcached.set(prefix + sessionId, exp, uid);
		return new UserSessionMetaData(sessionId, uid);
	}
}
