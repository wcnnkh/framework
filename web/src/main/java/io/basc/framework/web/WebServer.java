package io.basc.framework.web;

import java.io.IOException;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.web.cors.Cors;
import io.basc.framework.web.cors.CorsRegistry;
import io.basc.framework.web.cors.CorsUtils;

public class WebServer implements WebService, Configurable {

	/**
	 * 高度服务
	 */
	private final ConfigurableServices<WebServiceDispatcher> dispatchers = new ConfigurableServices<>(
			WebServiceDispatcher.class);
	/**
	 * 终止服务
	 */
	private final ConfigurableServices<WebServiceTerminator> terminators = new ConfigurableServices<>(
			WebServiceTerminator.class);
	/**
	 * 拦截器
	 */
	private final ConfigurableServices<WebServiceInterceptor> interceptors = new ConfigurableServices<>(
			WebServiceInterceptor.class);
	private CorsRegistry corsRegistry;

	private boolean configurabled;

	public WebServer() {
		getTerminators().registerLast(new HttpNotFoundHandler());
	}

	@Override
	public void service(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException, WebException {
		if (corsRegistry != null && serverRequest instanceof ServerHttpRequest
				&& serverResponse instanceof ServerHttpResponse) {
			cors((ServerHttpRequest) serverRequest, (ServerHttpResponse) serverResponse);
		}

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

	protected void cors(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		if (!CorsUtils.isCorsRequest(request)) {
			return;
		}

		if (!corsRegistry.test(request)) {
			return;
		}

		Cors cors = corsRegistry.process(request);
		if (cors == null) {
			return;
		}

		cors.write(request, response.getHeaders());
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
		if (corsRegistry == null) {
			corsRegistry = serviceLoaderFactory.getBeanProvider(CorsRegistry.class).getUnique().orElse(null);
		}

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
