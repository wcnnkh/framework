package scw.cache;

import scw.memcached.Memcached;
import scw.memcached.MemcachedLock;

public final class MemcachedCacheFactory extends AbstractCacheFactory{
	private final Memcached memcached;

	public MemcachedCacheFactory(int exp, Memcached memcached, boolean lock){
		super(exp, lock);
		this.memcached = memcached;
	}
	
	public void delete(String key) {
		memcached.delete(key);
	}
	
	private <T> T getByCache(String key){
		if(getExp() > 0){
			return memcached.getAndTocuh(key, getExp());
		}else{
			return memcached.get(key);
		}
	}
	
	private void setToCache(String key, Object value){
		if(getExp() > 0){
			memcached.set(key, getExp(), value);
		}else{
			memcached.set(key, value);
		}
	}
	
	public <T> T get(String key) {
		CallableInfo<T> callableInfo = getCallable(key);
		if(callableInfo == null){
			return null;
		}
		
		T t = getByCache(key);
		if(t == null){
			if(isLock()){
				MemcachedLock lock = new MemcachedLock(memcached, key);
				try {
					lock.lockWait(10);
					t = getByCache(key);
					if(t == null){
						t = callableInfo.getCallable().call();
						if(t != null){
							setToCache(key, t);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					lock.unLock();
				}
			}else{
				try {
					t = callableInfo.getCallable().call();
					if(t != null){
						setToCache(key, t);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return t;
	}
}
