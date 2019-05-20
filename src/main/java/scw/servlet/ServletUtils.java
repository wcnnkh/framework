package scw.servlet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.core.DefaultKeyValuePair;
import scw.core.KeyValuePair;
import scw.core.KeyValuePairFilter;
import scw.core.LinkedMultiValueMap;
import scw.core.MultiValueMap;
import scw.core.net.http.ContentType;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

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
	 * 判断是否是AJAX请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	/**
	 * 判断是否是json请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isJsonRequest(HttpServletRequest request) {
		return isDesignatedContentType(request,
				scw.core.net.http.ContentType.APPLICATION_JSON);
	}

	public static boolean isFormRequest(HttpServletRequest request) {
		return isDesignatedContentType(request,
				ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
	}

	public static boolean isMultipartRequest(HttpServletRequest request) {
		return isDesignatedContentType(request, ContentType.MULTIPART_FORM_DATA);
	}

	public static boolean isDesignatedContentType(HttpServletRequest request,
			String contentType) {
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

	public static ServletService getServletService(BeanFactory beanFactory,
			PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) {
		if (isAsyncSupport()) {
			return beanFactory
					.get("scw.servlet.AsyncServletService", beanFactory,
							propertiesFactory, configPath, rootBeanFilters);
		} else {
			return beanFactory
					.get("scw.servlet.DefaultServletService", beanFactory,
							propertiesFactory, configPath, rootBeanFilters);
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
	public static Cookie getCookie(HttpServletRequest request, String name,
			boolean ignoreCase) {
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

	public static Map<String, String> getRequestFirstValueParameters(
			ServletRequest request, KeyValuePairFilter<String, String> filter) {
		Map<String, String[]> requestParams = request.getParameterMap();
		if (requestParams == null || requestParams.isEmpty()) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();
		for (Entry<String, String[]> entry : requestParams.entrySet()) {
			String name = entry.getKey();
			if (name == null) {
				continue;
			}

			String[] values = entry.getValue();
			if (values == null || values.length == 0) {
				continue;
			}

			KeyValuePair<String, String> keyValuePair = filter
					.filter(new DefaultKeyValuePair<String, String>(name,
							values[0]));
			if (keyValuePair == null) {
				continue;
			}

			map.put(keyValuePair.getKey(), keyValuePair.getValue());
		}
		return map;
	}

	public static MultiValueMap<String, String> getRequestParameters(
			ServletRequest request, KeyValuePairFilter<String, String[]> filter) {
		Map<String, String[]> requestParams = request.getParameterMap();
		if (requestParams == null || requestParams.isEmpty()) {
			return null;
		}

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>(
				requestParams.size(), 1);
		for (Entry<String, String[]> entry : requestParams.entrySet()) {
			String name = entry.getKey();
			if (name == null) {
				continue;
			}

			String[] values = entry.getValue();
			if (values == null || values.length == 0) {
				continue;
			}

			if (filter == null) {
				map.put(name, new LinkedList<String>(Arrays.asList(values)));
			} else {
				KeyValuePair<String, String[]> keyValuePair = filter
						.filter(new DefaultKeyValuePair<String, String[]>(name,
								values));
				if (keyValuePair == null) {
					continue;
				}

				map.put(keyValuePair.getKey(),
						new LinkedList<String>(Arrays.asList(keyValuePair
								.getValue())));
			}
		}
		return map;
	}

	public static Map<String, String> getRequestParameterAndAppendValues(
			ServletRequest request, CharSequence appendValueChars,
			KeyValuePairFilter<String, String[]> filter) {
		if (filter == null) {
			Map<String, String[]> requestParams = request.getParameterMap();
			if (CollectionUtils.isEmpty(requestParams)) {
				return null;
			}

			Map<String, String> params = new HashMap<String, String>(
					requestParams.size(), 1);
			for (Entry<String, String[]> entry : requestParams.entrySet()) {
				String name = entry.getKey();
				if (name == null) {
					continue;
				}

				String[] values = entry.getValue();
				if (values == null || values.length == 0) {
					continue;
				}

				if (appendValueChars == null) {
					params.put(name, values[0]);
				} else {
					StringBuilder sb = new StringBuilder();
					for (String value : values) {
						if (sb.length() != 0) {
							sb.append(appendValueChars);
						}

						sb.append(value);
					}
					params.put(name, sb.toString());
				}
			}
			return params;
		} else {
			MultiValueMap<String, String> requestParams = getRequestParameters(
					request, filter);
			if (CollectionUtils.isEmpty(requestParams)) {
				return null;
			}

			Map<String, String> params = new HashMap<String, String>(
					requestParams.size(), 1);
			for (Entry<String, List<String>> entry : requestParams.entrySet()) {
				String name = entry.getKey();
				if (name == null) {
					continue;
				}

				List<String> values = entry.getValue();
				if (CollectionUtils.isEmpty(values)) {
					continue;
				}

				if (appendValueChars == null) {
					params.put(name, requestParams.getFirst(name));
				} else {
					StringBuilder sb = new StringBuilder();
					for (String value : values) {
						if (sb.length() != 0) {
							sb.append(appendValueChars);
						}

						sb.append(value);
					}
					params.put(name, sb.toString());
				}
			}
			return params;
		}
	}

	/**
	 * 判断是否是HttpServlet
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static boolean isHttpServlet(ServletRequest request,
			ServletResponse response) {
		return request instanceof HttpServletRequest
				&& response instanceof HttpServletResponse;
	}

	public static void jsp(ServletRequest request, ServletResponse response,
			String page) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getRequestObjectParameterWrapper(Request request,
			Class<T> type, String name) {
		try {
			return (T) privateRequestObjectParameterWrapper(request, type,
					StringUtils.isEmpty(name) ? null : name + ".");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Object privateRequestObjectParameterWrapper(Request request,
			Class<?> type, String prefix) throws Exception {
		if (!ReflectUtils.isInstance(type)) {
			return null;
		}

		Object t = ReflectUtils.newInstance(type);
		Class<?> clz = type;
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())
						|| Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				String key = prefix == null ? field.getName() : prefix
						+ field.getName();
				if (String.class.isAssignableFrom(field.getType())
						|| ClassUtils.isPrimitiveOrWrapper(field.getType())) {
					// 如果是基本数据类型
					Object v = request.getParameter(field.getType(), key);
					if (v != null) {
						ReflectUtils.setFieldValue(clz, field, t, v);
					}
				} else {
					ReflectUtils.setFieldValue(
							clz,
							field,
							t,
							privateRequestObjectParameterWrapper(request,
									field.getType(), key + "."));
				}
			}
			clz = clz.getSuperclass();
		}
		return t;
	}
}
