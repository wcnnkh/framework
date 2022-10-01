package io.basc.framework.websocket.adapter.standard;

import javax.websocket.server.ServerEndpointConfig.Configurator;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.FactoryLoader;

/**
 * 使用容器来管理websocket
 * 
 * @author shuchaowen
 *
 */
public class StandardContainerConfigurator extends Configurator {

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		BeanFactory beanFactory = FactoryLoader.getBeanFactory(endpointClass.getClassLoader());
		if (beanFactory == null) {
			beanFactory = FactoryLoader.getBeanFactory();
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
