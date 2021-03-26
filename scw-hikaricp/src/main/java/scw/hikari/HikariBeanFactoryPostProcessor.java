package scw.hikari;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.db.DB;
import scw.db.DBUtils;

@Provider
public class HikariBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		BeanDefinition definition = new HikariCPDBBeanDefinitaion(beanFactory);
		if(!beanFactory.containsDefinition(definition.getId())){
			beanFactory.registerDefinition(definition);
			if(!beanFactory.isAlias(DB.class.getName())){
				beanFactory.registerAlias(definition.getId(), DB.class.getName());
			}
		}
	}

	private static class HikariCPDBBeanDefinitaion extends DefaultBeanDefinition {

		public HikariCPDBBeanDefinitaion(BeanFactory beanFactory) {
			super(beanFactory, HikariDB.class);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION);
		}

		public Object create() throws BeansException {
			return new HikariDB(DBUtils.DEFAULT_CONFIGURATION);
		}
	}
}
