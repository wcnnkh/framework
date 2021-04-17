package scw.rmi.beans;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.core.utils.StringUtils;

@Provider
public class RmiClientBeanfactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		for (Class<?> clazz : beanFactory.getContextClassesLoader()) {
			RmiClient client = clazz.getAnnotation(RmiClient.class);
			if (client == null) {
				continue;
			}

			if (!beanFactory.containsDefinition(clazz.getName())) {
				String host = StringUtils.EMPTY.negate().first(client.host(), client.value());
				beanFactory.registerDefinition(new RmiClientBeanDefinition(beanFactory, clazz, host));
			}
		}
	}

}
