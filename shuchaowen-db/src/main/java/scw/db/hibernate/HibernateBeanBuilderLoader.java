package scw.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;

import scw.beans.annotation.Bean;
import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.loader.BeanBuilderLoader;
import scw.beans.loader.BeanBuilderLoaderChain;
import scw.beans.loader.LoaderContext;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
@Bean(proxy = false)
public class HibernateBeanBuilderLoader implements BeanBuilderLoader {

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain serviceChain) {
		if (context.getTargetClass() == org.hibernate.cfg.Configuration.class) {
			return new ConfigurationBeanBuilder(context);
		} else if (context.getTargetClass() == SessionFactory.class) {
			return new SessionFactoryBeanBuilder(context);
		}
		return serviceChain.loading(context);
	}

	private static final class SessionFactoryBeanBuilder extends
			AbstractBeanBuilder {

		public SessionFactoryBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return beanFactory
					.isInstance(org.hibernate.cfg.Configuration.class);
		}

		public Object create() throws Exception {
			org.hibernate.cfg.Configuration configuration = beanFactory
					.getInstance(org.hibernate.cfg.Configuration.class);
			if (beanFactory.isInstance(ServiceRegistry.class)) {
				return configuration.buildSessionFactory(beanFactory
						.getInstance(ServiceRegistry.class));
			} else {
				return configuration.buildSessionFactory();
			}
		}
		
		@Override
		public void destroy(Object instance) throws Exception {
			if(instance instanceof SessionFactory){
				((SessionFactory) instance).close();
			}
			super.destroy(instance);
		}
	}

	private static final class ConfigurationBeanBuilder extends
			AbstractBeanBuilder {

		public ConfigurationBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws Exception {
			return new org.hibernate.cfg.Configuration().configure();
		}
	}

}
