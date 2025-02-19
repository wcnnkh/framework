package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.context.Context;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.http.server.jsonp.JsonpUtils;
import io.basc.framework.mvc.security.UserSessionManager;
import io.basc.framework.net.multipart.MultipartMessageResolver;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.support.DefaultWebMessageConverters;
import io.basc.framework.web.pattern.HttpPatternMatcher;

public class DefaultHttpChannelFactory implements HttpChannelFactory {
	private MultipartMessageResolver multipartMessageResolver;
	private final HttpPatternMatcher<Boolean> jsonpSupportConfig = new HttpPatternMatcher<Boolean>();
	private final HttpPatternMatcher<Boolean> jsonSupportWrapperConfig = new HttpPatternMatcher<Boolean>();
	private final HttpPatternMatcher<Boolean> multipartFormSupportWrapperConfig = new HttpPatternMatcher<Boolean>();
	private final WebMessageConverters webMessageConverters;
	private final UserSessionManager userSessionManager;
	private final Context context;

	public DefaultHttpChannelFactory(Context context) {
		this.context = context;
		webMessageConverters = new DefaultWebMessageConverters(context);
		webMessageConverters.configure(context);
		this.userSessionManager = context.isInstance(UserSessionManager.class)
				? context.getInstance(UserSessionManager.class)
				: null;
	}

	public MultipartMessageResolver getMultipartMessageResolver() {
		return multipartMessageResolver;
	}

	public void setMultipartMessageResolver(MultipartMessageResolver multipartMessageResolver) {
		this.multipartMessageResolver = multipartMessageResolver;
	}

	public final HttpPatternMatcher<Boolean> getJsonpSupportConfig() {
		return jsonpSupportConfig;
	}

	public final HttpPatternMatcher<Boolean> getJsonSupportWrapperConfig() {
		return jsonSupportWrapperConfig;
	}

	public final HttpPatternMatcher<Boolean> getMultipartFormSupportWrapperConfig() {
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
		return new DefaultHttpChannel(context, requestToUse, responseToUse, webMessageConverters, userSessionManager);
	}
}
