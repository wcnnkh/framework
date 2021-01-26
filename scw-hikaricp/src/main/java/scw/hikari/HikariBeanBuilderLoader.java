package scw.hikari;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.db.DB;
import scw.db.DBUtils;
import scw.io.ResourceUtils;

@Provider(order = Integer.MIN_VALUE)
public class HikariBeanBuilderLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == HikariDB.class) {
			return new HikariCPDBBeanDefinitaion(beanFactory, sourceClass);
		} else if (DB.class == sourceClass) {
			return beanFactory.getDefinition(HikariDB.class);
		}
		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class HikariCPDBBeanDefinitaion extends DefaultBeanDefinition {

		public HikariCPDBBeanDefinitaion(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return ResourceUtils.exists(beanFactory.getEnvironment(), DBUtils.DEFAULT_CONFIGURATION);
		}

		public Object create() throws BeansException {
			return new HikariDB(DBUtils.DEFAULT_CONFIGURATION);
		}
	}
}
