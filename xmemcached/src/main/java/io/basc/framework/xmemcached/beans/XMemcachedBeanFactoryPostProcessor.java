package io.basc.framework.xmemcached.beans;

import java.io.IOException;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.io.SerializerUtils;
import io.basc.framework.xmemcached.MyTranscoder;
import io.basc.framework.xmemcached.XMemcached;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.transcoders.Transcoder;

@Provider
public class XMemcachedBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		BeanDefinition clientDefinition = new MemcachedClientBeanDefinition(beanFactory);
		if (!beanFactory.containsDefinition(clientDefinition.getId())) {
			beanFactory.registerDefinition(clientDefinition);
		}

		BeanDefinition builderDefinition = new XMemcachedClientBuilderBeanDefinition(beanFactory);
		if (!beanFactory.containsDefinition(builderDefinition.getId())) {
			beanFactory.registerDefinition(builderDefinition);

			if (!beanFactory.containsDefinition(MemcachedClientBuilder.class.getName())) {
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
			String name = beanFactory.getEnvironment().getValue("memcached.hosts.config.name", String.class,
					"memcached.hosts");
			return beanFactory.getEnvironment().getString(name);
		}

		public Object create() throws BeansException {
			XMemcachedClientBuilder builder = XMemcached.builder(getHosts());
			builderDefault(builder);
			return builder;
		}

		private void builderDefault(XMemcachedClientBuilder builder) {
			if (beanFactory.isInstance(Transcoder.class)) {
				builder.setTranscoder(beanFactory.getInstance(Transcoder.class));
			} else {
				builder.setTranscoder(new MyTranscoder(SerializerUtils.getSerializer()));
			}

			Integer poolSize = beanFactory.getEnvironment().getInteger("memcached.poolsize");
			if (poolSize != null) {
				builder.setConnectionPoolSize(poolSize);
			}
		}
	}
}
