package io.basc.framework.websocket.adapter.standard;

import javax.websocket.server.ServerEndpointConfig.Configurator;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.support.FactoryLoader;

/**
 * 使用容器来管理websocket
 * 
 * @author wcnnkh
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

		return beanFactory.getBean(endpointClass);
	}
}
