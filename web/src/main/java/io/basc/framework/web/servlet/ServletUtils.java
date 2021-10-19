package io.basc.framework.web.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.http.ContentDisposition;
import io.basc.framework.http.HttpCookie;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.lang.Constants;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
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

	public static HttpCookie wrapper(Cookie cookie) {
		return new HttpCookie(cookie.getName(), cookie.getValue()).setDomain(cookie.getDomain())
				.setMaxAge(cookie.getMaxAge()).setSecure(cookie.getSecure()).setPath(cookie.getPath());
	}

	public static HttpServletRequest getHttpServletRequest(ServerHttpRequest request) {
		return XUtils.getDelegate(request, HttpServletRequest.class);
	}

	public static HttpServletResponse getHttpServletResponse(ServerHttpResponse response) {
		return XUtils.getDelegate(response, HttpServletResponse.class);
	}

	public static ServletService createServletService(BeanFactory beanFactory) {
		if (beanFactory.isInstance(ServletService.class)) {
			return beanFactory.getInstance(ServletService.class);
		}
		return new DefaultHttpServletService(beanFactory);
	}

	/**
	 * 将文件信息写入ContentDisposition
	 * 
	 * @param outputMessage
	 * @param fileName
	 */
	public static void writeFileMessageHeaders(HttpServletResponse response, String fileName) {
		MimeType mimeType = FileMimeTypeUitls.getMimeType(fileName);
		if (mimeType != null) {
			response.setContentType(mimeType.toString());
		}
		ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
				.filename(fileName, Constants.UTF_8).build();
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
	}
}
