package scw.net.http.server.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.net.http.HttpHeaders;
import scw.net.http.server.servlet.ServletServerHttpRequest;

public final class ServletUtils {
	private static final boolean asyncSupport = ClassUtils.isPresent("javax.servlet.AsyncContext");// 是否支持异步处理
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

	/**
	 * 从cookie中获取数据
	 * 
	 * @param request
	 * 
	 * @param name
	 *            cookie中的名字
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		if (name == null) {
			return null;
		}

		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie == null) {
				continue;
			}

			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	/**
	 * 判断是否是HttpServlet
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static boolean isHttpServlet(ServletRequest request, ServletResponse response) {
		return request instanceof HttpServletRequest && response instanceof HttpServletResponse;
	}

	public static void jsp(ServletRequest request, ServletResponse response, String page)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}

	public static String getIP(HttpServletRequest httpServletRequest) {
		return new ServletServerHttpRequest(httpServletRequest).getIp();
	}

	/**
	 * 判断是否是AJAX请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader(HttpHeaders.X_REQUESTED_WITH));
	}

	/**
	 * 判断是不是一个weboskcet请求
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	public static boolean isWebSocketRequest(HttpServletRequest httpServletRequest) {
		String value = httpServletRequest.getHeader(HttpHeaders.UPGRADE);
		return StringUtils.equals(value, "websocket", true);
	}

	public static String getForwardRequestUri(ServletRequest servletRequest) {
		return (String) servletRequest.getAttribute(ATTRIBUTE_FORWARD_REQUEST_URI);
	}
}
