package run.soeasy.framework.beans.factory.config;

import run.soeasy.framework.beans.BeansException;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor{

	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
