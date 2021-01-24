package scw.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.io.Resource;

@Provider(order = Integer.MIN_VALUE)
public class HibernateBeanBuilderLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain serviceChain) {
		if (sourceClass == org.hibernate.cfg.Configuration.class) {
			return new ConfigurationBeanBuilder(beanFactory, sourceClass);
		} else if (sourceClass == SessionFactory.class) {
			return new SessionFactoryBeanBuilder(beanFactory, sourceClass);
		}
		return serviceChain.load(beanFactory, sourceClass);
	}

	private static final class SessionFactoryBeanBuilder extends DefaultBeanDefinition {

		public SessionFactoryBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(org.hibernate.cfg.Configuration.class);
		}

		public Object create() throws Exception {
			org.hibernate.cfg.Configuration configuration = beanFactory
					.getInstance(org.hibernate.cfg.Configuration.class);
			if (beanFactory.isInstance(ServiceRegistry.class)) {
				return configuration.buildSessionFactory(beanFactory.getInstance(ServiceRegistry.class));
			} else {
				return configuration.buildSessionFactory();
			}
		}

		@Override
		public void destroy(Object instance) throws Throwable {
			if (instance instanceof SessionFactory) {
				((SessionFactory) instance).close();
			}
			super.destroy(instance);
		}
	}

	private static final class ConfigurationBeanBuilder extends DefaultBeanDefinition {
		
		public ConfigurationBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);
		}

		public Object create() throws Exception {
			Resource resource = beanFactory.getEnvironment()
					.getResource(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);
			return new org.hibernate.cfg.Configuration().configure(resource.getURL());
		}
	}

}
