package shuchaowen.core.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class XMemcached implements Memcached{
	private final MemcachedClient memcachedClient;
	private final boolean abnormalInterruption;
	
	/**
	 * @param memcachedClient
	 * @param abnormalInterruption 发生异常时是否中断
	 */
	public XMemcached(MemcachedClient memcachedClient, boolean abnormalInterruption){
		this.memcachedClient = memcachedClient;
		this.abnormalInterruption = abnormalInterruption;
	}
	
	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public <T> T get(String key) {
		try {
			return memcachedClient.get(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return null;
	}

	public <T> CAS<T> gets(String key) {
		GetsResponse<T> cas;
		try {
			cas = memcachedClient.gets(key);
			return new CAS<T>(cas.getCas(), cas.getValue());
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean set(String key, Object value) {
		try {
			return memcachedClient.set(key, 0, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean set(String key, int exp, Object data) {
		try {
			return memcachedClient.set(key, exp, data);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean add(String key, Object value) {
		try {
			return memcachedClient.add(key, 0, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean add(String key, int exp, Object data) {
		try {
			return memcachedClient.add(key, exp, data);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean cas(String key, Object data, long cas) {
		try {
			return memcachedClient.cas(key, 0, data, cas);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean cas(String key, int exp, Object data, long cas) {
		try {
			return memcachedClient.cas(key, exp, data, cas);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}

	public <T> T getAndTocuh(String key, int newExp) {
		try {
			return memcachedClient.getAndTouch(key, newExp);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean touch(String key, int exp) {
		try {
			return memcachedClient.touch(key, exp);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return false;
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		try {
			return memcachedClient.get(keyCollections);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		return null;
	}

	public long incr(String key, long incr) {
		try {
			 return memcachedClient.incr(key, incr);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		throw new ShuChaoWenRuntimeException("incr error key[" + key + "] incr[" + incr + "]");
	}


	public long decr(String key, long decr) {
		try {
			return memcachedClient.incr(key, decr);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		throw new ShuChaoWenRuntimeException("decr error key[" + key + "] decr[" + decr + "]");
	}

	public long incr(String key, long incr, long initValue) {
		try {
			return memcachedClient.incr(key, incr, initValue);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		throw new ShuChaoWenRuntimeException("incr error key[" + key + "] incr[" + incr + "] initValue[" + initValue + "]");
	}

	public long decr(String key, long decr, long initValue) {
		try {
			return memcachedClient.incr(key, decr, initValue);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		throw new ShuChaoWenRuntimeException("decr error key[" + key + "] decr[" + decr + "] initValue[" + initValue + "]");
	}
	
	public <T> Map<String, CAS<T>> gets(Collection<String> keyCollections) {
		Map<String, GetsResponse<T>> map = null;
		try {
			map = memcachedClient.gets(keyCollections);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}
		
		if(map != null){
			Map<String, CAS<T>> casMap = new HashMap<String, CAS<T>>();
			for(Entry<String, GetsResponse<T>> entry : map.entrySet()){
				casMap.put(entry.getKey(), new CAS<T>(entry.getValue().getCas(), entry.getValue().getValue()));
			}
			return casMap;
		}
		return null;
	}

	public boolean delete(String key) {
		try {
			memcachedClient.delete(key);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		return false;
	}

}
