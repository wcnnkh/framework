package io.basc.framework.web.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.basc.framework.env.Environment;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.servlet.http.DefaultHttpServletService;

public final class ServletUtils {
	private static final boolean asyncSupport = ClassUtils.isPresent("javax.servlet.AsyncContext", null);// 是否支持异步处理
	public static final String ATTRIBUTE_FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";

	private ServletUtils() {
	};

	/**
	 * 是否支持异步处理(实际是否支持还要判断request)
	 * 
	 * @return
	 */
	public static boolean isAsyncSupport() {
		return asyncSupport;
	}

	public static void forward(ServletRequest request, ServletResponse response, String path)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

	public static String getForwardRequestUri(ServletRequest servletRequest) {
		return (String) servletRequest.getAttribute(ATTRIBUTE_FORWARD_REQUEST_URI);
	}

	public static HttpServletRequest getHttpServletRequest(ServerHttpRequest request) {
		return XUtils.getDelegate(request, HttpServletRequest.class);
	}

	public static HttpServletResponse getHttpServletResponse(ServerHttpResponse response) {
		return XUtils.getDelegate(response, HttpServletResponse.class);
	}

	public static ServletService createServletService(ServiceLoaderFactory factory, Environment environment) {
		if (factory.isInstance(ServletService.class)) {
			return factory.getInstance(ServletService.class);
		}
		return new DefaultHttpServletService(factory, environment);
	}
}
