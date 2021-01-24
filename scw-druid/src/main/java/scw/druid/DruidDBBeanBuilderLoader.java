package scw.druid;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.db.DB;
import scw.db.DBUtils;
import scw.io.ResourceUtils;

@Provider(order = Integer.MIN_VALUE + 1)
public class DruidDBBeanBuilderLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == DruidDB.class) {
			return new DruidDBBeanDefinition(beanFactory, sourceClass);
		} else if (DB.class == sourceClass) {
			return beanFactory.getBeanDefinition(DruidDB.class);
		}
		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class DruidDBBeanDefinition extends DefaultBeanDefinition {

		public DruidDBBeanDefinition(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return ResourceUtils.exists(beanFactory.getEnvironment(), DBUtils.DEFAULT_CONFIGURATION);
		}

		public Object create() throws Exception {
			return new DruidDB(DBUtils.DEFAULT_CONFIGURATION);
		}
	}
}
