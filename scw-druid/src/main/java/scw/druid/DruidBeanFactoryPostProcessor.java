package scw.druid;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.db.DB;
import scw.db.DBUtils;

@Provider(order = Integer.MIN_VALUE + 1)
public class DruidBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		BeanDefinition definition = new DruidDBBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(definition.getId())){
			beanFactory.registerDefinition(definition);
			if(!beanFactory.isAlias(DB.class.getName())){
				beanFactory.registerAlias(definition.getId(), DB.class.getName());
			}
		}
	}

	private static class DruidDBBeanDefinition extends DefaultBeanDefinition {

		public DruidDBBeanDefinition(BeanFactory beanFactory) {
			super(beanFactory, DruidDB.class);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DBUtils.DEFAULT_CONFIGURATION);
		}

		public Object create() throws BeansException {
			return new DruidDB(DBUtils.DEFAULT_CONFIGURATION);
		}
	}
}
