package scw.servlet;

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
import scw.servlet.mvc.http.MyHttpServletRequest;

public final class ServletUtils {
	private static boolean asyncSupport = true;// 是否支持异步处理

	static {
		try {
			ClassUtils.forName("javax.servlet.AsyncContext");
		} catch (Throwable e) {
			asyncSupport = false;// 不支持
		}
	}

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
	 * @param ignoreCase
	 *            查找时是否忽略大小写
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String name, boolean ignoreCase) {
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

			if (ignoreCase) {
				if (name.equalsIgnoreCase(cookie.getName())) {
					return cookie;
				}
			} else {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
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
		return new MyHttpServletRequest(httpServletRequest).getIP();
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
	 * @param httpServletRequest
	 * @return
	 */
	public static boolean isWebSocketRequest(HttpServletRequest httpServletRequest){
		String value = httpServletRequest.getHeader(HttpHeaders.UPGRADE);
		return StringUtils.equals(value, "websocket", true);
	}
}
