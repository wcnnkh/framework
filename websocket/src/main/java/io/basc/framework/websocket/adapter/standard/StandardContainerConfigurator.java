package io.basc.framework.websocket.adapter.standard;

import javax.websocket.server.ServerEndpointConfig.Configurator;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.support.ContextLoader;

/**
 * 使用容器来管理websocket
 * 
 * @author wcnnkh
 *
 */
public class StandardContainerConfigurator extends Configurator {

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		ApplicationContext applicationContext = ContextLoader.getCurrentApplicationContext();
		if (applicationContext == null) {
			applicationContext = ContextLoader.getApplicationContext(endpointClass.getClassLoader());
		}

		if (applicationContext == null) {
			throw new InstantiationException("Not found application context");
		}

		return applicationContext.getBean(endpointClass);
	}
}
