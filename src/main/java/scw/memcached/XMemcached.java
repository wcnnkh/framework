package scw.memcached;

import java.io.IOException;

/**
 * 此类已弃用
 * @author shuchaowen
 *
 */
public final class XMemcached extends scw.data.memcached.x.XMemcached implements Memcached {
	/**
	 * 获取一个本地的memcached对象 localhost:11211
	 * 
	 * @throws IOException
	 */
	public XMemcached() throws IOException {
		this("localhost:11211");
	}

	public XMemcached(String hosts) throws IOException {
		super(hosts);
	}

	/**
	 * * 设置连接池大小，即客户端个数 In a high concurrent enviroment,you may want to pool
	 * memcached clients. But a xmemcached client has to start a reactor thread
	 * and some thread pools, if you create too many clients,the cost is very
	 * large. Xmemcached supports connection pool instreadof client pool. you
	 * can create more connections to one or more memcached servers, and these
	 * connections share the same reactor and thread pools, it will reduce the
	 * cost of system. 默认的pool
	 * size是1。设置这一数值不一定能提高性能，请依据你的项目的测试结果为准。初步的测试表明只有在大并发下才有提升。
	 * 设置连接池的一个不良后果就是，同一个memcached的连接之间的数据更新并非同步的
	 * 因此你的应用需要自己保证数据更新的原子性（采用CAS或者数据之间毫无关联）。
	 * 
	 * @param hosts
	 * @param poolSize
	 * @throws IOException
	 */
	public XMemcached(String hosts, int poolSize) throws IOException {
		super(hosts, poolSize);
	}
}
