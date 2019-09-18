package scw.mvc.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.utils.StringUtils;
import scw.mvc.Filter;
import scw.mvc.MVCUtils;
import scw.mvc.ParameterFilter;
import scw.mvc.servlet.http.DefaultHttpServletChannelFactory;
import scw.mvc.servlet.http.HttpServletChannelFactory;

public final class ServletUtils {
	private static boolean asyncSupport = true;// 是否支持异步处理

	static {
		try {
			Class.forName("javax.servlet.AsyncContext");
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

	public static ServletService getServletService(BeanFactory beanFactory, PropertyFactory propertyFactory,
			boolean async) {
		if (async) {
			return beanFactory.getInstance("scw.mvc.servlet.AsyncServletService");
		} else {
			return beanFactory.getInstance("scw.mvc.servlet.DefaultServletService");
		}
	}

	public static ServletService getServletService(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		return getServletService(beanFactory, propertyFactory,
				isAsyncSupport() && StringUtils.parseBoolean(propertyFactory.getProperty("servlet.async")));
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

	public static HttpServletChannelFactory getHttpServletChannelFactory(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		String factoryName = propertyFactory.getProperty("mvc.servlet.http.channel-factory");
		if (StringUtils.isEmpty(factoryName)) {
			return new DefaultHttpServletChannelFactory(beanFactory, MVCUtils.isDebug(propertyFactory),
					getParameterFilters(beanFactory, propertyFactory),
					MVCUtils.getJsonParseSupport(beanFactory, propertyFactory),
					MVCUtils.isSupportCookieValue(propertyFactory), MVCUtils.getJsonp(propertyFactory));
		} else {
			return beanFactory.getInstance(factoryName);
		}
	}

	public static Collection<Filter> getFilters(InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<Filter> filters = MVCUtils.getFilters(instanceFactory, propertyFactory);
		BeanUtils.appendBean(filters, instanceFactory, propertyFactory, "servlet.filters");
		return filters;
	}

	public static Collection<ParameterFilter> getParameterFilters(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		LinkedList<ParameterFilter> filters = MVCUtils.getParameterFilters(instanceFactory, propertyFactory,
				"servlet.parameter.filters");
		filters.addAll(MVCUtils.getParameterFilters(instanceFactory, propertyFactory));
		return filters;
	}

	public static String getIP(HttpServletRequest httpServletRequest) {
		String ip = httpServletRequest.getHeader("x-forwarded-for");
		return ip == null ? httpServletRequest.getRemoteAddr() : ip;
	}

	/**
	 * 判断是否是AJAX请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	public static String formatContextPathUrl(ServletRequest servletRequest, String url) {
		if (StringUtils.isEmpty(url)) {
			return servletRequest.getServletContext().getContextPath();
		} else if (url.startsWith("/")) {
			return servletRequest.getServletContext().getContextPath() + url;
		} else {
			return url;
		}
	}
}
