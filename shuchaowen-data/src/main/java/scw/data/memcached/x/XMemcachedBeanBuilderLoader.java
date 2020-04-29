package scw.data.memcached.x;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import scw.beans.annotation.Bean;
import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.io.serialzer.SerializerUtils;
import scw.net.NetworkUtils;

@Configuration(order = Integer.MIN_VALUE)
@Bean(proxy = false)
public class XMemcachedBeanBuilderLoader implements BeanBuilderLoader {

	public BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == MemcachedClientBuilder.class
				|| context.getTargetClass() == XMemcachedClientBuilder.class) {
			return new MemcachedClientBuilderBeanBuilder(context);
		} else if (context.getTargetClass() == MemcachedClient.class) {
			return new MemcachedClientBeanBuilder(context);
		}
		return loaderChain.loading(context);
	}

	private static final class MemcachedClientBeanBuilder extends AbstractBeanBuilder {

		public MemcachedClientBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(MemcachedClientBuilder.class);
		}

		public Object create() throws Exception {
			return beanFactory.getInstance(MemcachedClientBuilder.class).build();
		}

		@Override
		public void destroy(Object instance) throws Exception {
			super.destroy(instance);
			if (instance instanceof MemcachedClient) {
				((MemcachedClient) instance).shutdown();
			}
		}
	}

	private static final class MemcachedClientBuilderBeanBuilder extends AbstractBeanBuilder {

		public MemcachedClientBuilderBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return getHosts() != null;
		}

		private String getHosts() {
			String name = propertyFactory.getValue("memcached.hosts.config.name", String.class, "memcached.hosts");
			return propertyFactory.getString(name);
		}

		public Object create() throws Exception {
			XMemcachedClientBuilder builder = new XMemcachedClientBuilder(
					NetworkUtils.parseInetSocketAddressList(getHosts()));
			builderDefault(builder);
			return builder;
		}

		private void builderDefault(XMemcachedClientBuilder builder) {
			// 宕机报警
			builder.setFailureMode(true);
			// 使用二进制文件
			builder.setCommandFactory(new BinaryCommandFactory());

			if (beanFactory.isInstance(Transcoder.class)) {
				builder.setTranscoder(beanFactory.getInstance(Transcoder.class));
			} else {
				builder.setTranscoder(new MyTranscoder(SerializerUtils.DEFAULT_SERIALIZER));
			}

			Integer poolSize = propertyFactory.getInteger("memcached.poolsize");
			if (poolSize != null) {
				builder.setConnectionPoolSize(poolSize);
			}
		}
	}
}
