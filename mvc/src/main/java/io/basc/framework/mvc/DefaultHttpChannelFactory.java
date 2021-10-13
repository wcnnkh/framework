package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.mvc.message.WebMessageConverters;
import io.basc.framework.mvc.message.support.DefaultWebMessageConverters;
import io.basc.framework.mvc.security.UserSessionManager;
import io.basc.framework.net.message.multipart.MultipartMessageResolver;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.jsonp.JsonpUtils;
import io.basc.framework.web.pattern.HttpPatterns;

public class DefaultHttpChannelFactory implements HttpChannelFactory {
	protected final BeanFactory beanFactory;
	private MultipartMessageResolver multipartMessageResolver;
	private final HttpPatterns<Boolean> jsonpSupportConfig = new HttpPatterns<Boolean>();
	private final HttpPatterns<Boolean> jsonSupportWrapperConfig = new HttpPatterns<Boolean>();
	private final HttpPatterns<Boolean> multipartFormSupportWrapperConfig = new HttpPatterns<Boolean>();
	private final WebMessageConverters webMessageConverters;
	private final UserSessionManager userSessionManager;

	public DefaultHttpChannelFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		webMessageConverters = new DefaultWebMessageConverters(beanFactory.getEnvironment().getConversionService(),
				beanFactory.getDefaultValueFactory());
		webMessageConverters.configure(beanFactory);
		this.userSessionManager = beanFactory.isInstance(UserSessionManager.class)
				? beanFactory.getInstance(UserSessionManager.class)
				: null;
	}

	public MultipartMessageResolver getMultipartMessageResolver() {
		return multipartMessageResolver;
	}

	public void setMultipartMessageResolver(MultipartMessageResolver multipartMessageResolver) {
		this.multipartMessageResolver = multipartMessageResolver;
	}

	public final HttpPatterns<Boolean> getJsonpSupportConfig() {
		return jsonpSupportConfig;
	}

	public final HttpPatterns<Boolean> getJsonSupportWrapperConfig() {
		return jsonSupportWrapperConfig;
	}

	public final HttpPatterns<Boolean> getMultipartFormSupportWrapperConfig() {
		return multipartFormSupportWrapperConfig;
	}

	public boolean isSupportJsonWrapper(ServerHttpRequest request) {
		return jsonSupportWrapperConfig.get(request, true);
	}

	public boolean isSupportJsonp(ServerHttpRequest request) {
		return jsonSupportWrapperConfig.get(request, true);
	}

	public boolean isSupportMultipartFormWrapper(ServerHttpRequest request) {
		return multipartFormSupportWrapperConfig.get(request, true);
	}

	public WebMessageConverters getWebMessageConverters() {
		return webMessageConverters;
	}

	public HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		ServerHttpRequest requestToUse = request;
		if (isSupportJsonWrapper(requestToUse)) {
			requestToUse = WebUtils.wrapperServerJsonRequest(requestToUse);
		}

		if (isSupportMultipartFormWrapper(requestToUse)) {
			requestToUse = WebUtils.wrapperServerMultipartFormRequest(requestToUse, getMultipartMessageResolver());
		}

		ServerHttpResponse responseToUse = response;
		if (isSupportJsonp(requestToUse)) {
			responseToUse = JsonpUtils.wrapper(requestToUse, responseToUse);
		}
		return new DefaultHttpChannel(beanFactory, requestToUse, responseToUse, webMessageConverters,
				userSessionManager);
	}
}
