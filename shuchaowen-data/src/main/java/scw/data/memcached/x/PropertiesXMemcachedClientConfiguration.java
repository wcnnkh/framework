package scw.data.memcached.x;

import java.util.Properties;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import scw.core.instance.InstanceFactory;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class PropertiesXMemcachedClientConfiguration implements XMemcachedClientConfiguration {
	private static Logger logger = LoggerUtils.getLogger(PropertiesXMemcachedClientConfiguration.class);
	private final XMemcachedClientConfiguration configuration;

	public PropertiesXMemcachedClientConfiguration(InstanceFactory instanceFactory,
			@ResourceParameter @DefaultValue("memcached.properties") String configurationFile) {
		this(instanceFactory, ResourceUtils.getResourceOperations().getFormattedProperties(configurationFile));
	}

	@SuppressWarnings("rawtypes")
	public PropertiesXMemcachedClientConfiguration(InstanceFactory instanceFactory, Properties properties) {
		String hosts = properties.getProperty("hosts");
		if (StringUtils.isEmpty(hosts)) {
			hosts = "localhost:11211";
		}

		int poolSize = StringUtils.parseInt(properties.getProperty("poolSize"));
		Transcoder transcoder = null;
		String transcoderName = properties.getProperty("transcoder");
		if (!StringUtils.isEmpty(transcoderName)) {
			if (instanceFactory.isInstance(transcoderName)) {
				transcoder = instanceFactory.getInstance(transcoderName);
			} else {
				logger.warn("无效的transcoder定义:{}", transcoderName);
			}
		}

		this.configuration = transcoder == null ? new DefaultXMemcachedClientConfiguration(hosts, poolSize)
				: new DefaultXMemcachedClientConfiguration(hosts, poolSize, transcoder);
	}

	public MemcachedClient configuration() throws Exception {
		return configuration.configuration();
	}
}
