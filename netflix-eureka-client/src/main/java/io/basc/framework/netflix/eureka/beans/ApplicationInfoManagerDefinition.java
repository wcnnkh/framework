package io.basc.framework.netflix.eureka.beans;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.netflix.eureka.InstanceInfoFactory;

public class ApplicationInfoManagerDefinition extends DefaultBeanDefinition {

	public ApplicationInfoManagerDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, ApplicationInfoManager.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(EurekaInstanceConfig.class);
	}

	@Override
	public Object create() throws BeansException {
		EurekaInstanceConfig eurekaInstanceConfig = beanFactory.getInstance(EurekaInstanceConfig.class);
		InstanceInfo instanceInfo = beanFactory.getInstance(InstanceInfoFactory.class).create(eurekaInstanceConfig);
		return new ApplicationInfoManager(eurekaInstanceConfig, instanceInfo);
	}
}