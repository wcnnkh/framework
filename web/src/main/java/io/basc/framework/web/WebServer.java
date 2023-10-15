package io.basc.framework.web;

import java.io.IOException;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServices;

public class WebServer implements WebService, Configurable {
	
	private final ConfigurableServices<WebServiceDispatcher> dispatchers = new ConfigurableServices<>(
			WebServiceDispatcher.class);
	private final ConfigurableServices<WebServiceTerminator> terminators = new ConfigurableServices<>(
			WebServiceTerminator.class);
	private final ConfigurableServices<WebServiceInterceptor> interceptors = new ConfigurableServices<>(
			WebServiceInterceptor.class);
	private boolean configurabled;

	@Override
	public void service(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException, WebException {
		WebService webService = dispatchers.getServices().filter((e) -> e.test(serverRequest)).first();
		if (webService == null) {
			webService = terminators.getServices().filter((e) -> e.test(serverRequest)).first();
		}

		WebServiceChain chain = new WebServiceChain(interceptors.getServices().iterator(), webService);
		try {
			chain.service(serverRequest, serverResponse);
		} finally {
			if (!serverResponse.isCommitted()) {
				if (serverRequest.isSupportAsyncControl()) {
					ServerAsyncControl serverAsyncControl = serverRequest.getAsyncControl(serverResponse);
					if (serverAsyncControl.isStarted()) {
						serverAsyncControl.addListener(new ServerResponseCompleteAsyncListener(serverResponse));
						return;
					}
				}
			}
			serverResponse.close();
		}
	}

	public ConfigurableServices<WebServiceDispatcher> getDispatchers() {
		return dispatchers;
	}

	public ConfigurableServices<WebServiceTerminator> getTerminators() {
		return terminators;
	}

	public ConfigurableServices<WebServiceInterceptor> getInterceptors() {
		return interceptors;
	}

	@Override
	public boolean isConfigured() {
		return configurabled;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		configurabled = true;
		if (!dispatchers.isConfigured()) {
			dispatchers.configure(serviceLoaderFactory);
		}

		if (!terminators.isConfigured()) {
			terminators.configure(serviceLoaderFactory);
		}

		if (!interceptors.isConfigured()) {
			interceptors.configure(serviceLoaderFactory);
		}
	}

}
