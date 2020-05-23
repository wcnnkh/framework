package scw.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class JmsBeanBuilderLoader implements BeanBuilderLoader {

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain) {
		if (Connection.class == context.getTargetClass()) {
			return new ConnectionBeanBuilder(context);
		}
		
		return loaderChain.loading(context);
	}

	private static class ConnectionBeanBuilder extends AbstractBeanBuilder {

		public ConnectionBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(ConnectionFactory.class);
		}

		public Object create() throws Exception {
			return beanFactory.getInstance(ConnectionFactory.class)
					.createConnection();
		}

		@Override
		public void destroy(Object instance) throws Exception {
			if (instance instanceof Connection) {
				((Connection) instance).close();
			}
		}
	}
}
