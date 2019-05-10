package scw.data.memcached.x;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import scw.beans.annotation.Destroy;
import scw.core.Constants;
import scw.core.serializer.Serializer;
import scw.core.utils.StringUtils;

public class XMemcached extends AbstractXMemcached {
	private final MemcachedClient memcachedClient;

	/**
	 * 获取一个本地的memcached对象 localhost:11211
	 * 
	 * @throws IOException
	 */
	public XMemcached() throws IOException {
		this("localhost:11211");
	}

	public XMemcached(String hosts) throws IOException {
		this(hosts, 1);
	}

	public XMemcached(String hosts, Serializer serializer) throws IOException {
		this(hosts, 1, serializer);
	}

	public XMemcached(String hosts, int poolSize) throws IOException {
		this(hosts, poolSize, Constants.DEFAULT_SERIALIZER);
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
	public XMemcached(String hosts, int poolSize, Serializer serializer) throws IOException {
		super(serializer);
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		String[] arr = StringUtils.commonSplit(hosts);
		for (String a : arr) {
			String[] vs = a.split(":");
			String h = vs[0];
			int port = 11211;
			if (vs.length == 2) {
				port = Integer.parseInt(vs[1]);
			}

			addresses.add(new InetSocketAddress(h, port));
		}

		MemcachedClientBuilder builder = new XMemcachedClientBuilder(addresses);
		// 宕机报警
		builder.setFailureMode(true);
		// 使用二进制文件
		builder.setCommandFactory(new BinaryCommandFactory());
		builder.setConnectionPoolSize(poolSize);
		this.memcachedClient = builder.build();
	}

	@Destroy
	public void destroy() throws IOException {
		memcachedClient.shutdown();
	}

	@Override
	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}
}
