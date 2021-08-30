package io.basc.framework.web.servlet.socket;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.ContextLoader;

import javax.websocket.server.ServerEndpointConfig.Configurator;

public class ContainerConfigurator extends Configurator {

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		BeanFactory beanFactory = ContextLoader.getCurrentBeanFactory();
		if (beanFactory == null) {
			beanFactory = ContextLoader.getBeanFactory(endpointClass.getClassLoader());
		}

		if (beanFactory == null) {
			throw new InstantiationException("Not found bean factory");
		}

		if (!beanFactory.isInstance(endpointClass)) {
			throw new InstantiationException("Not instance " + endpointClass.getName());
		}

		return beanFactory.getInstance(endpointClass);
	}
}
