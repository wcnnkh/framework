package io.basc.framework.netflix.eureka.beans;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.boot.Application;
import io.basc.framework.netflix.eureka.CloudEurekaClient;

public class EurekaClientDefinition extends DefaultBeanDefinition {

	public EurekaClientDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, EurekaClient.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(ApplicationInfoManager.class)
				&& beanFactory.isInstance(EurekaClientConfig.class) && beanFactory.isInstance(Application.class);
	}

	@Override
	public Object create() throws BeansException {
		ApplicationInfoManager applicationInfoManager = beanFactory.getInstance(ApplicationInfoManager.class);
		EurekaClientConfig eurekaClientConfig = beanFactory.getInstance(EurekaClientConfig.class);
		Application application = beanFactory.getInstance(Application.class);
		return new CloudEurekaClient(applicationInfoManager, eurekaClientConfig, application);
	}
}