package scw.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;

@Provider(order = Integer.MIN_VALUE)
public class JmsBeanBuilderLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain) {
		if (Connection.class == sourceClass) {
			return new ConnectionBeanBuilder(beanFactory, sourceClass);
		}
		
		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class ConnectionBeanBuilder extends DefaultBeanDefinition {

		public ConnectionBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(ConnectionFactory.class);
		}

		public Object create() throws Exception {
			return beanFactory.getInstance(ConnectionFactory.class)
					.createConnection();
		}

		@Override
		public void destroy(Object instance) throws Throwable {
			if (instance instanceof Connection) {
				((Connection) instance).close();
			}
			super.destroy(instance);
		}
	}
}
