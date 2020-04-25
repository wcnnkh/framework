package scw.data.memcached.x;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import scw.beans.annotation.Bean;
import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.loader.BeanBuilderLoader;
import scw.beans.loader.BeanBuilderLoaderChain;
import scw.beans.loader.LoaderContext;
import scw.core.Constants;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.io.SerializerUtils;
import scw.net.NetworkUtils;

@Configuration(order = Integer.MIN_VALUE)
@Bean(proxy = false)
public class XMemcachedBeanBuilderLoader implements BeanBuilderLoader {
	private static final String HOST_NAME = "memcached.hosts";

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == MemcachedClientBuilder.class) {
			return loading(new LoaderContext(XMemcachedClientBuilder.class,
					context), loaderChain);
		} else if (context.getTargetClass() == XMemcachedClientBuilder.class) {
			return new MemcachedClientBuilderBeanBuilder(context);
		} else if (context.getTargetClass() == MemcachedClient.class) {
			return new MemcachedClientBeanBuilder(context);
		}
		return loaderChain.loading(context);
	}

	private static final class MemcachedClientBeanBuilder extends
			AbstractBeanBuilder {

		public MemcachedClientBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(MemcachedClientBuilder.class);
		}

		public Object create() throws Exception {
			return beanFactory.getInstance(MemcachedClientBuilder.class)
					.build();
		}

		@Override
		public void destroy(Object instance) throws Exception {
			super.destroy(instance);
			if (instance instanceof MemcachedClient) {
				((MemcachedClient) instance).shutdown();
			}
		}
	}

	private static final class MemcachedClientBuilderBeanBuilder extends
			AbstractBeanBuilder {

		public MemcachedClientBuilderBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws Exception {
			String hosts = propertyFactory.getString(HOST_NAME);
			if (StringUtils.isEmpty(hosts)) {
				hosts = "127.0.0.1:11211";
			}

			XMemcachedClientBuilder builder = new XMemcachedClientBuilder(
					NetworkUtils.parseInetSocketAddressList(hosts));
			// 宕机报警
			builder.setFailureMode(true);
			// 使用二进制文件
			builder.setCommandFactory(new BinaryCommandFactory());

			if (beanFactory.isInstance(Transcoder.class)) {
				builder.setTranscoder(beanFactory.getInstance(Transcoder.class));
			} else {
				builder.setTranscoder(new MyTranscoder(
						SerializerUtils.DEFAULT_SERIALIZER));
			}
			builder.setConnectionPoolSize(Constants.AVAILABLE_PROCESSORS);
			return builder;
		}
	}
}
