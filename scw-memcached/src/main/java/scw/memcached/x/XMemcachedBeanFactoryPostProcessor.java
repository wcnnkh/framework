package scw.memcached.x;

import java.io.IOException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.io.SerializerUtils;
import scw.net.InetUtils;

@Provider
public class XMemcachedBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		BeanDefinition clientDefinition = new MemcachedClientBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(clientDefinition.getId())){
			beanFactory.registerDefinition(clientDefinition);
		}
		
		BeanDefinition builderDefinition = new XMemcachedClientBuilderBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(builderDefinition.getId())){
			beanFactory.registerDefinition(builderDefinition);
			
			if(!beanFactory.containsDefinition(MemcachedClientBuilder.class.getName())){
				beanFactory.registerAlias(builderDefinition.getId(), MemcachedClientBuilder.class.getName());
			}
		}
	}

	private static final class MemcachedClientBeanDefinition extends DefaultBeanDefinition {

		public MemcachedClientBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, MemcachedClient.class);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(MemcachedClientBuilder.class);
		}

		public Object create() throws BeansException {
			try {
				return beanFactory.getInstance(MemcachedClientBuilder.class).build();
			} catch (IOException e) {
				throw new BeansException(e);
			}
		}

		@Override
		public void destroy(Object instance) throws BeansException {
			super.destroy(instance);
			if (instance instanceof MemcachedClient) {
				try {
					((MemcachedClient) instance).shutdown();
				} catch (IOException e) {
					throw new BeansException(e);
				}
			}
		}
	}

	private static final class XMemcachedClientBuilderBeanDefinition extends DefaultBeanDefinition {

		public XMemcachedClientBuilderBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, XMemcachedClientBuilder.class);
		}

		public boolean isInstance() {
			return getHosts() != null;
		}

		private String getHosts() {
			String name = beanFactory.getEnvironment().getValue("memcached.hosts.config.name", String.class, "memcached.hosts");
			return beanFactory.getEnvironment().getString(name);
		}

		public Object create() throws BeansException {
			XMemcachedClientBuilder builder = new XMemcachedClientBuilder(
					InetUtils.parseInetSocketAddressList(getHosts()));
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
				builder.setTranscoder(new MyTranscoder(SerializerUtils.getSerializer().toCodec()));
			}

			Integer poolSize = beanFactory.getEnvironment().getInteger("memcached.poolsize");
			if (poolSize != null) {
				builder.setConnectionPoolSize(poolSize);
			}
		}
	}
}
