package scw.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import scw.beans.annotation.Destroy;
import scw.common.utils.StringUtils;

public final class XMemcached implements Memcached{
	private final MemcachedClient memcachedClient;
	
	/**
	 * 获取一个本地的memcached对象
	 * localhost:11211
	 * @throws IOException
	 */
	public XMemcached() throws IOException{
		this("localhost:11211");
	}
	
	public XMemcached(String hosts) throws IOException{
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		String[] arr = StringUtils.commonSplit(hosts);
		for(String a : arr){
			InetSocketAddress address = new InetSocketAddress(a.split(":")[0], Integer.parseInt(a.split(":")[1]));
			addresses.add(address);
		}
	
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(addresses);
		// 宕机报警
		builder.setFailureMode(true);
		// 使用二进制文件
		builder.setCommandFactory(new BinaryCommandFactory());
		/*
		 * * 设置连接池大小，即客户端个数 In a high concurrent enviroment,you may want to pool
		 * memcached clients. But a xmemcached client has to start a reactor
		 * thread and some thread pools, if you create too many clients,the cost
		 * is very large. Xmemcached supports connection pool instreadof client
		 * pool. you can create more connections to one or more memcached
		 * servers, and these connections share the same reactor and thread
		 * pools, it will reduce the cost of system. 默认的pool
		 * size是1。设置这一数值不一定能提高性能，请依据你的项目的测试结果为准。初步的测试表明只有在大并发下才有提升。
		 * 设置连接池的一个不良后果就是，同一个memcached的连接之间的数据更新并非同步的
		 * 因此你的应用需要自己保证数据更新的原子性（采用CAS或者数据之间毫无关联）。
		 */
		this.memcachedClient = builder.build();
	}
	
	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public <T> T get(String key){
		try {
			return memcachedClient.get(key);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public <T> CAS<T> gets(String key) {
		GetsResponse<T> cas;
		try {
			cas = memcachedClient.gets(key);
			if(cas == null){
				return null;
			}
			return new CAS<T>(cas.getCas(), cas.getValue());
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean set(String key, Object value) {
		try {
			return memcachedClient.set(key, 0, value);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean set(String key, int exp, Object data) {
		try {
			return memcachedClient.set(key, exp, data);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean add(String key, Object value) {
		try {
			return memcachedClient.add(key, 0, value);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean add(String key, int exp, Object data) {
		try {
			return memcachedClient.add(key, exp, data);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean cas(String key, Object data, long cas) {
		try {
			return memcachedClient.cas(key, 0, data, cas);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean cas(String key, int exp, Object data, long cas) {
		try {
			return memcachedClient.cas(key, exp, data, cas);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public <T> T getAndTocuh(String key, int newExp) {
		//因为可能不支持此协议
		T t;
		try {
			t = memcachedClient.get(key);
			if(t != null){
				memcachedClient.set(key, newExp, t);
			}
			return t;
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean touch(String key, int exp) {
		try {
			return memcachedClient.touch(key, exp);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		try {
			return memcachedClient.get(keyCollections);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public long incr(String key, long incr) {
		try {
			 return memcachedClient.incr(key, incr);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public long decr(String key, long decr) {
		try {
			return memcachedClient.incr(key, decr);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public long incr(String key, long incr, long initValue) {
		try {
			return memcachedClient.incr(key, incr, initValue);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public long decr(String key, long decr, long initValue) {
		try {
			return memcachedClient.incr(key, decr, initValue);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}
	
	public <T> Map<String, CAS<T>> gets(Collection<String> keyCollections) {
		Map<String, GetsResponse<T>> map = null;
		try {
			map = memcachedClient.gets(keyCollections);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
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
			return memcachedClient.delete(key);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}

	public boolean delete(String key, long cas, long opTimeout) {
		try {
			return memcachedClient.delete(key, cas, opTimeout);
		} catch (TimeoutException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.memcached.MemcachedException(e);
		}
	}
	
	@Destroy
	public void destroy() throws IOException{
		memcachedClient.shutdown();
	}
}
