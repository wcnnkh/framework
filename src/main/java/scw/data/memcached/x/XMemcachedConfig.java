package scw.data.memcached.x;

import java.util.Properties;

import scw.beans.BeanFactory;
import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class XMemcachedConfig {
	private static Logger logger = LoggerUtils.getLogger(XMemcachedConfig.class);
	private String hosts;
	private Integer poolSize;
	private MyTranscoder transcoder;

	public XMemcachedConfig(BeanFactory beanFactory, @ParameterName("memcached.configuration") String configuration) {
		Properties properties = ResourceUtils.getProperties(configuration);
		String hosts = properties.getProperty("hosts");
		if (StringUtils.isEmpty(hosts)) {
			this.hosts = "localhost:11211";
		}

		this.poolSize = StringUtils.parseInt(properties.getProperty("poolSize"), null);
		String transcoderName = properties.getProperty("transcoder");
		if (!StringUtils.isEmpty(transcoderName)) {
			if (beanFactory.isInstance(transcoderName)) {
				this.transcoder = beanFactory.getInstance(transcoderName);
			} else {
				logger.warn("无效的transcoder定义:{}", transcoderName);
			}
		}
	}

	/**
	 * 设置连接池大小，即客户端个数 In a high concurrent enviroment,you may want to pool
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
	 * @param transcoder
	 * @param keyPrefix
	 */
	public XMemcachedConfig(@ParameterName("memcached.hosts") String hosts,
			@ParameterName("memcached.pool.size") @NotRequire Integer poolSize,
			@ParameterName("memcached.transcoder") @NotRequire MyTranscoder transcoder) {
		this.hosts = hosts;
		this.poolSize = poolSize;
		this.transcoder = transcoder;
	}

	public String getHosts() {
		return hosts;
	}

	public Integer getPoolSize() {
		return poolSize;
	}

	public MyTranscoder getTranscoder() {
		return transcoder;
	}
}
