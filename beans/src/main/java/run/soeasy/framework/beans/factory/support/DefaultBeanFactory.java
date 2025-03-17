package run.soeasy.framework.beans.factory.support;

import run.soeasy.framework.beans.factory.NoSuchBeanDefinitionException;
import run.soeasy.framework.beans.factory.config.BeanDefinition;

public class DefaultBeanFactory extends AbstractListableBeanFactory {

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition beanDefinition = getBeanDefinitionOfCache(beanName);
		if (beanDefinition == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
		return beanDefinition;
	}
}
