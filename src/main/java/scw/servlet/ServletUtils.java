package scw.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.common.utils.StringUtils;
import scw.net.http.enums.ContentType;

public final class ServletUtils {
	private static final String ASYNCCONTEXT_NAME = "javax.servlet.AsyncContext";
	private static final String SERVLET_SERVICE_BEAN_NAME = "scw.servlet.DefaultServletService";
	private static final String ASYNC_SERVLET_SERVICE_BEAN_ANEM = "scw.servlet.AsyncServletService";

	private static final String AJAX_HEADER_NAME = "X-Requested-With";
	private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

	private static boolean asyncSupport = true;// 是否支持异步处理

	static {
		try {
			Class.forName(ASYNCCONTEXT_NAME);
		} catch (Throwable e) {
			asyncSupport = false;// 不支持
		}
	}

	private ServletUtils() {
	};

	/**
	 * 判断是否是AJAX请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return AJAX_HEADER_VALUE.equals(request.getHeader(AJAX_HEADER_NAME));
	}

	/**
	 * 判断是否是json请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isJsonRequest(HttpServletRequest request) {
		return isDesignatedContentType(request, ContentType.JSON.getValue());
	}

	public static boolean isFormRequest(HttpServletRequest request) {
		return isDesignatedContentType(request, ContentType.FORM.getValue());
	}

	public static boolean isMultipartRequest(HttpServletRequest request) {
		return isDesignatedContentType(request, ContentType.MULTIPART.getValue());
	}

	public static boolean isDesignatedContentType(HttpServletRequest request, String contentType) {
		String ct = request.getContentType();
		return StringUtils.isEmpty(ct) ? false : ct.startsWith(contentType);
	}

	/**
	 * 获取ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		return ip == null ? request.getRemoteAddr() : ip;
	}

	/**
	 * 是否支持异步处理
	 * 
	 * @return
	 */
	public static boolean isAsyncSupport() {
		return asyncSupport;
	}

	public static ServletService getServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory,
			String configPath, String[] rootBeanFilters) {
		if (isAsyncSupport()) {
			return beanFactory.get(ASYNC_SERVLET_SERVICE_BEAN_ANEM, beanFactory, propertiesFactory, configPath,
					rootBeanFilters);
		} else {
			return beanFactory.get(SERVLET_SERVICE_BEAN_NAME, beanFactory, propertiesFactory, configPath,
					rootBeanFilters);
		}
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
}
