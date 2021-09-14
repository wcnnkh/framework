package io.basc.framework.rmi.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.util.StringUtils;

@Provider
public class RmiClientBeanfactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		for (Class<?> clazz : beanFactory.getContextClasses()) {
			RmiClient client = clazz.getAnnotation(RmiClient.class);
			if (client == null) {
				continue;
			}

			if (!beanFactory.containsDefinition(clazz.getName())) {
				String host = StringUtils.IS_EMPTY.negate().first(client.host(), client.value());
				beanFactory.registerDefinition(new RmiClientBeanDefinition(beanFactory, clazz, host));
			}
		}
	}

}
