package scw.data.memcached.x;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import scw.core.annotation.ParameterName;
import scw.io.serializer.SerializerUtils;
import scw.net.NetworkUtils;

@SuppressWarnings("rawtypes")
public final class DefaultXMemcachedClientConfiguration implements XMemcachedClientConfiguration {
	private String hosts;
	private int poolSize;
	private Transcoder transcoder;

	public DefaultXMemcachedClientConfiguration(@ParameterName("memcached.hosts") String hosts) {
		this(hosts, -1);
	}

	public DefaultXMemcachedClientConfiguration(String hosts, int poolSize) {
		this(hosts, -1, new MyTranscoder(SerializerUtils.DEFAULT_SERIALIZER));
	}

	public DefaultXMemcachedClientConfiguration(String hosts, int poolSize, Transcoder transcoder) {
		this.hosts = hosts;
		this.poolSize = poolSize;
		this.transcoder = transcoder;
	}

	public MemcachedClient configuration() throws Exception {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(NetworkUtils.parseInetSocketAddressList(hosts));
		// 宕机报警
		builder.setFailureMode(true);
		// 使用二进制文件
		builder.setCommandFactory(new BinaryCommandFactory());

		if (transcoder != null) {
			builder.setTranscoder(transcoder);
		}

		if (poolSize > 0) {
			builder.setConnectionPoolSize(poolSize);
		}
		return builder.build();
	}
}
