package scw.druid;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.db.DB;
import scw.db.DBUtils;

@Provider(order = Integer.MIN_VALUE + 1)
public class DruidDBBeanDefinitionLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == DruidDB.class) {
			return new DruidDBBeanDefinition(beanFactory, sourceClass);
		} else if (DB.class == sourceClass) {
			return beanFactory.getDefinition(DruidDB.class);
		}
		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class DruidDBBeanDefinition extends DefaultBeanDefinition {

		public DruidDBBeanDefinition(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION);
		}

		public Object create() throws BeansException {
			return new DruidDB(DBUtils.DEFAULT_CONFIGURATION);
		}
	}
}
