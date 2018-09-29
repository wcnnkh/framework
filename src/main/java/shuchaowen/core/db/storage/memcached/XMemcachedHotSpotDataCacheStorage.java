package shuchaowen.core.db.storage.memcached;

import java.io.IOException;

import net.rubyeye.xmemcached.MemcachedClient;
import shuchaowen.core.db.storage.AbstractHotSpotDataCacheStorage;
import shuchaowen.core.util.XTime;

public class XMemcachedHotSpotDataCacheStorage extends AbstractHotSpotDataCacheStorage{
	private MemcachedClient memcachedClient;
	
	/**
	 * 连接localhost
	 * @throws IOException
	 */
	public XMemcachedHotSpotDataCacheStorage() throws IOException{
		this(new LocalXMmecached().getMemcachedClient());
	}

	/**
	 * 热点数据 过期时间7天
	 * 
	 * @param memcachedClient
	 */
	public XMemcachedHotSpotDataCacheStorage(MemcachedClient memcachedClient) {
		this((int) ((7 * XTime.ONE_DAY) / 1000), memcachedClient);
	}

	public XMemcachedHotSpotDataCacheStorage(int exp, MemcachedClient memcachedClient) {
		this("", exp, memcachedClient);
	}

	public XMemcachedHotSpotDataCacheStorage(String prefix, int exp, MemcachedClient memcachedClient) {
		super(prefix, exp);
		this.memcachedClient = memcachedClient;
	}

	@Override
	public <T> T getAndTouch(Class<T> type, String key, int exp) throws Exception{
		T t = memcachedClient.get(key);;
		if (t == null) {
			return t;
		}

		if (exp > 0) {
			memcachedClient.set(key, exp, t);
		}
		return t;
	}

	@Override
	public void set(String key, int exp, Object data) throws Exception{
		memcachedClient.add(key, exp, data);
	}

	@Override
	public boolean add(String key, int exp, Object data) throws Exception{
		return memcachedClient.set(key, exp, data);
	}

	@Override
	public boolean delete(String key) throws Exception{
		return memcachedClient.delete(key);
	}

}
