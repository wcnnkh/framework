package io.basc.framework.xmemcached.beans;

import java.io.IOException;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.io.SerializerUtils;
import io.basc.framework.xmemcached.MyTranscoder;
import io.basc.framework.xmemcached.XMemcached;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.transcoders.Transcoder;

@Provider
public class XMemcachedContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		BeanDefinition clientDefinition = new MemcachedClientBeanDefinition(context);
		if (!context.containsDefinition(clientDefinition.getId())) {
			context.registerDefinition(clientDefinition);
		}

		BeanDefinition builderDefinition = new XMemcachedClientBuilderBeanDefinition(context);
		if (!context.containsDefinition(builderDefinition.getId())) {
			context.registerDefinition(builderDefinition);

			if (!context.containsDefinition(MemcachedClientBuilder.class.getName())) {
				context.registerAlias(builderDefinition.getId(), MemcachedClientBuilder.class.getName());
			}
		}
	}

	private static final class MemcachedClientBeanDefinition extends FactoryBeanDefinition {

		public MemcachedClientBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, MemcachedClient.class);
		}

		public boolean isInstance() {
			return getBeanFactory().isInstance(MemcachedClientBuilder.class);
		}

		public Object create() throws BeansException {
			try {
				return getBeanFactory().getInstance(MemcachedClientBuilder.class).build();
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

	private static final class XMemcachedClientBuilderBeanDefinition extends FactoryBeanDefinition {
		private Environment environment;

		public XMemcachedClientBuilderBeanDefinition(Environment environment) {
			super(environment, XMemcachedClientBuilder.class);
			this.environment = environment;
		}

		public boolean isInstance() {
			return getHosts() != null;
		}

		private String getHosts() {
			String name = environment.getProperties().get("memcached.hosts.config.name").or("memcached.hosts")
					.getAsString();
			return environment.getProperties().getAsString(name);
		}

		public Object create() throws BeansException {
			XMemcachedClientBuilder builder = XMemcached.builder(getHosts());
			builderDefault(builder);
			return builder;
		}

		private void builderDefault(XMemcachedClientBuilder builder) {
			if (environment.isInstance(Transcoder.class)) {
				builder.setTranscoder(environment.getInstance(Transcoder.class));
			} else {
				builder.setTranscoder(new MyTranscoder(SerializerUtils.getSerializer()));
			}

			environment.getProperties().get("memcached.poolsize").as(Integer.class)
					.ifPresent((e) -> builder.setConnectionPoolSize(e));
		}
	}
}
