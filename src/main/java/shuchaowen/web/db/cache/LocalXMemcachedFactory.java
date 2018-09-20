package shuchaowen.web.db.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import shuchaowen.core.db.cache.Cache;
import shuchaowen.core.db.cache.CacheFactory;
import shuchaowen.core.util.XTime;

/**
 * 此类使用memcached    连接localhost和默认的端口号来实现
 * 数据缓存时间为15天
 * @author shuchaowen
 *
 */
public class LocalXMemcachedFactory implements CacheFactory{
	private XMemcachedCache xMemcachedCache;
	
	public LocalXMemcachedFactory() throws IOException{
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
			InetSocketAddress address = new InetSocketAddress("localhost", 11211);
			addresses.add(address);
		
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
		builder.setConnectionPoolSize(10);
		
		xMemcachedCache = new XMemcachedCache((int)(15 * XTime.ONE_DAY/1000), builder.build());
	}
	
	public Cache getCache(Class<?> tableClass) {
		return xMemcachedCache;
	}
}
