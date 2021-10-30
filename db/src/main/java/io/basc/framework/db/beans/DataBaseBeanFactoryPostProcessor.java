package io.basc.framework.db.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeanlifeCycleEvent.Step;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.db.Configurable;
import io.basc.framework.db.DB;
import io.basc.framework.sql.orm.SqlDialect;



@Provider
public class DataBaseBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(DB.class.getName())) {
			beanFactory.registerDefinition(new DataBaseDefinition(beanFactory));
		}

		if (!beanFactory.containsDefinition(Configurable.class.getName())) {
			beanFactory.registerDefinition(new ConfigurableDefinition(beanFactory));
		}
		
		//自动初始化依赖
		beanFactory.getLifecycleDispatcher().registerListener((e) -> {
			if(e.getStep() == Step.BEFORE_INIT && e.getSource() != null && e.getSource() instanceof SqlDialect && e.getSource() instanceof io.basc.framework.factory.Configurable){
				((io.basc.framework.factory.Configurable)e.getSource()).configure(e.getBeanFactory());
			}
		});
	}

}
