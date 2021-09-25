package io.basc.framework.netflix.eureka.beans;

import com.netflix.discovery.EurekaClientConfig;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.netflix.eureka.EurekaClientConfigBean;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EureakBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		EurekaClientDefinition eurekaClientDefinition = new EurekaClientDefinition(beanFactory);
		if(!beanFactory.containsDefinition(eurekaClientDefinition.getId())) {
			beanFactory.registerDefinition(eurekaClientDefinition);
		}
		
		ApplicationInfoManagerDefinition applicationInfoManagerDefinition = new ApplicationInfoManagerDefinition(beanFactory);
		if(!beanFactory.containsDefinition(applicationInfoManagerDefinition.getId())) {
			beanFactory.registerDefinition(applicationInfoManagerDefinition);
		}
		
		if(!beanFactory.isAlias(EurekaClientConfig.class.getName())) {
			beanFactory.registerAlias(EurekaClientConfigBean.class.getName(), EurekaClientConfig.class.getName());
		}
		
		EurekaInstanceConfigBeanDefinition eurekaInstanceConfigDefinition = new EurekaInstanceConfigBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(eurekaInstanceConfigDefinition.getId())) {
			beanFactory.registerDefinition(eurekaInstanceConfigDefinition);
		}
	}

}
