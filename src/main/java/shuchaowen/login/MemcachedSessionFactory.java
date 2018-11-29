package shuchaowen.login;

import shuchaowen.core.util.StringUtils;
import shuchaowen.core.util.XUtils;
import shuchaowen.memcached.Memcached;

public class MemcachedSessionFactory implements SessionFactory{
	private final Memcached memcached;
	private final String prefix;
	private final int exp;
	
	public MemcachedSessionFactory(Memcached memcached, String prefix, int exp){
		this.memcached = memcached;
		this.prefix = prefix;
		this.exp = exp;
	}
	
	public Session getSession(String sessionId) {
		if(StringUtils.isNull(sessionId)){
			return null;
		}
		
		Long uid = memcached.getAndTocuh(prefix + sessionId, exp);
		if(uid == null){
			return null;
		}
		
		return new Session(sessionId, uid);
	}

	public Session login(long uid) {
		String newSid = uid + XUtils.getUUID();
		memcached.set(prefix + newSid, exp, uid);
		return new Session(newSid, uid);
	}

	public void cancelLogin(String sessionId) {
		if(sessionId == null){
			return ;
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
}
