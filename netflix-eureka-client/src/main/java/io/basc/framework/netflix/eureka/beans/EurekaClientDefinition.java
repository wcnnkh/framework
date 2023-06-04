package io.basc.framework.netflix.eureka.beans;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.boot.Application;
import io.basc.framework.netflix.eureka.CloudEurekaClient;

public class EurekaClientDefinition extends FactoryBeanDefinition {

	public EurekaClientDefinition(BeanFactory beanFactory) {
		super(beanFactory, EurekaClient.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(ApplicationInfoManager.class)
				&& getBeanFactory().isInstance(EurekaClientConfig.class)
				&& getBeanFactory().isInstance(Application.class);
	}

	@Override
	public Object create() throws BeansException {
		ApplicationInfoManager applicationInfoManager = getBeanFactory().getInstance(ApplicationInfoManager.class);
		EurekaClientConfig eurekaClientConfig = getBeanFactory().getInstance(EurekaClientConfig.class);
		Application application = getBeanFactory().getInstance(Application.class);
		return new CloudEurekaClient(applicationInfoManager, eurekaClientConfig, application.getEventDispatcher());
	}
}