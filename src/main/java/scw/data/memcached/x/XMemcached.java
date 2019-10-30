package scw.data.memcached.x;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import scw.core.utils.StringUtils;
import scw.data.cas.CASOperations;
import scw.data.memcached.AbstractMemcachedWrapper;
import scw.data.memcached.Memcached;
import scw.io.SerializerUtils;

public final class XMemcached extends AbstractMemcachedWrapper implements scw.core.Destroy {
	private final Memcached memcached;
	private final MemcachedClient memcachedClient;
	private final CASOperations casOperations;

	public XMemcached(String hosts) throws IOException {
		this(new XMemcachedConfig(hosts, null, new MyTranscoder(SerializerUtils.DEFAULT_SERIALIZER)));
	}

	public XMemcached(XMemcachedConfig config) throws IOException {
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		String[] arr = StringUtils.commonSplit(config.getHosts());
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
		if (config.getTranscoder() != null) {
			builder.setTranscoder(config.getTranscoder());
		}

		if (config.getPoolSize() != null) {
			builder.setConnectionPoolSize(config.getPoolSize());
		}

		this.memcachedClient = builder.build();
		this.memcached = new XMemcachedImpl(memcachedClient);
		this.casOperations = new XMemcachedCASOperations(memcachedClient);
	}

	public void destroy() {
		try {
			memcachedClient.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Memcached getTargetMemcached() {
		return memcached;
	}

	@Override
	public CASOperations getCASOperations() {
		return casOperations;
	}
}
