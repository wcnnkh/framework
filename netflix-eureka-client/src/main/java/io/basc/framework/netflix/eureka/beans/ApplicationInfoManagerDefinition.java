package io.basc.framework.netflix.eureka.beans;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.netflix.eureka.InstanceInfoFactory;

public class ApplicationInfoManagerDefinition extends FactoryBeanDefinition {

	public ApplicationInfoManagerDefinition(BeanFactory beanFactory) {
		super(beanFactory, ApplicationInfoManager.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(EurekaInstanceConfig.class);
	}

	@Override
	public Object create() throws BeansException {
		EurekaInstanceConfig eurekaInstanceConfig = getBeanFactory().getInstance(EurekaInstanceConfig.class);
		InstanceInfo instanceInfo = getBeanFactory().getInstance(InstanceInfoFactory.class)
				.create(eurekaInstanceConfig);
		return new ApplicationInfoManager(eurekaInstanceConfig, instanceInfo);
	}
}