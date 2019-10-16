package scw.mvc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanDefinition;
import scw.beans.BeanUtils;
import scw.core.Constants;
import scw.core.Destroy;
import scw.core.KeyValuePair;
import scw.core.KeyValuePairFilter;
import scw.core.PropertyFactory;
import scw.core.SimpleKeyValuePair;
import scw.core.ValueFactory;
import scw.core.annotation.ParameterName;
import scw.core.attribute.Attributes;
import scw.core.context.Context;
import scw.core.context.ContextManager;
import scw.core.context.support.ThreadLocalContextManager;
import scw.core.exception.BeansException;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.multivalue.LinkedMultiValueMap;
import scw.core.multivalue.MultiValueMap;
import scw.core.parameter.ContainAnnotationParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectUtils;
import scw.core.resource.ResourceUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;
import scw.mvc.action.HttpNotFoundService;
import scw.mvc.action.ResponseWrapperFilter;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Filters;
import scw.mvc.annotation.Model;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.Text;
import scw.mvc.http.filter.CrossDomainDefinition;
import scw.mvc.http.filter.HttpActionServiceFilter;
import scw.mvc.parameter.ParameterFilter;
import scw.mvc.parameter.ParameterFilterChain;
import scw.mvc.parameter.SimpleParameterParseFilterChain;
import scw.net.header.HeadersConstants;
import scw.net.mime.MimeTypeConstants;
import scw.rpc.RpcService;

public final class MVCUtils implements MvcConstants {
	private static Logger logger = LoggerUtils.getLogger(MVCUtils.class);

	private MVCUtils() {
	};

	private static final ContextManager MVC_CONTEXT_MANAGER = new ThreadLocalContextManager(true);

	public static Channel getContextChannel() {
		Context context = MVC_CONTEXT_MANAGER.getCurrentContext();
		return (Channel) (context == null ? null : context.getResource(Channel.class));
	}

	public static Context getContext() {
		return MVC_CONTEXT_MANAGER.getCurrentContext();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> getAttributeMap(Attributes attributes) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<String> enumeration = attributes.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			if (name != null || isSystemAttribute(name)) {
				continue;
			}

			Object value = attributes.getAttribute(name);
			if (value == null) {
				continue;
			}
			map.put(name, value);
		}
		return map;
	}

	public static boolean isSystemAttribute(String name) {
		return RESTURL_PATH_PARAMETER.equals(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setHttpAuthorityId(Attributes attributes, String id) {
		attributes.setAttribute(HTTP_AUTHORITY_ATTRIBUTE_NAME, id);
	}

	public static Collection<ExceptionHandler> getExceptionHandlers(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		LinkedList<ExceptionHandler> exceptionHandlers = new LinkedList<ExceptionHandler>();
		BeanUtils.appendBean(exceptionHandlers, instanceFactory, propertyFactory, ExceptionHandler.class,
				"mvc.exception.handler");
		if (instanceFactory.isInstance(ExceptionHandler.class) && instanceFactory.isSingleton(ExceptionHandler.class)) {
			exceptionHandlers.add(instanceFactory.getInstance(ExceptionHandler.class));
		}
		return exceptionHandlers;
	}

	public static void service(Collection<Filter> filters, Channel channel, int warnExecuteTime,
			Collection<ExceptionHandler> exceptionHandlers) {
		long t = System.currentTimeMillis();
		FilterChain filterChain = new SimpleFilterChain(filters);
		Context context = MVC_CONTEXT_MANAGER.createContext();
		context.bindResource(Channel.class, channel);
		try {
			channel.write(filterChain.doFilter(channel));
		} catch (Throwable e) {
			ExceptionHandlerChain exceptionHandlerChain = new ExceptionHandlerChain(exceptionHandlers);
			Object errorResult = exceptionHandlerChain.doHandler(channel, e);
			try {
				channel.write(errorResult);
			} catch (Throwable e1) {
				channel.getLogger().error(e1, channel.toString());
			}
		} finally {
			try {
				if (channel instanceof Destroy) {
					((Destroy) channel).destroy();
				}
			} finally {
				MVC_CONTEXT_MANAGER.release(context);
				t = System.currentTimeMillis() - t;
				if (t > warnExecuteTime) {
					channel.getLogger().warn("执行{}超时，用时{}ms", channel.toString(), t);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getParameterWrapper(ValueFactory<String> request, Class<T> type, String name) {
		try {
			return (T) privateParameterWrapper(request, type,
					StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name : name + "."));
		} catch (Exception e) {
			throw new RuntimeException("参数错误:" + type.getName(), e);
		}
	}

	public static void parameterWrapper(Object instance, ValueFactory<String> request, Class<?> type, String name) {
		try {
			privateParameterWrapper(instance, request, type,
					StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name : name + "."));
		} catch (Exception e) {
			throw new RuntimeException("参数错误:" + type.getName(), e);
		}
	}

	private static void privateParameterWrapper(Object instance, ValueFactory<String> request, Class<?> type,
			String prefix) throws Exception {
		Class<?> clz = type;
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				ReflectUtils.setAccessibleField(field);
				if (!field.getType().isPrimitive() && field.get(instance) != null) {
					continue;
				}

				String fieldName = field.getName();
				ParameterName parameterName = field.getAnnotation(ParameterName.class);
				if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
					fieldName = parameterName.value();
				}

				String key = StringUtils.isEmpty(prefix) ? fieldName : prefix + fieldName;
				if (String.class.isAssignableFrom(field.getType())
						|| ClassUtils.isPrimitiveOrWrapper(field.getType())) {
					// 濡傛灉鏄熀鏈暟鎹被鍨�
					Object v = XUtils.getValue(request, key, field.getType());
					if (v != null) {
						ReflectUtils.setFieldValue(clz, field, instance, v);
					}
				} else {
					ReflectUtils.setFieldValue(clz, field, instance,
							privateParameterWrapper(request, field.getType(), key + "."));
				}
			}
			clz = clz.getSuperclass();
		}
	}

	private static Object privateParameterWrapper(ValueFactory<String> request, Class<?> type, String prefix)
			throws Exception {
		if (!ReflectUtils.isInstance(type)) {
			return null;
		}

		Object t = InstanceUtils.newInstance(type);
		privateParameterWrapper(t, request, type, prefix);
		return t;
	}

	public static Constructor<?> getModelConstructor(Class<?> type) {
		Constructor<?>[] constructors = type.getDeclaredConstructors();
		Constructor<?> constructor = null;
		if (constructors.length == 1) {
			constructor = constructors[0];
		} else {
			for (int i = 0; i < constructors.length; i++) {
				constructor = constructors[i];
				Model model = constructor.getAnnotation(Model.class);
				if (model == null) {
					continue;
				}

				break;
			}
		}
		return constructor;
	}

	public static Object[] getParameterValues(Channel channel, ContainAnnotationParameterConfig[] parameterDefinitions,
			Collection<ParameterFilter> parameterFilters) throws Throwable {
		Object[] args = new Object[parameterDefinitions.length];
		for (int i = 0; i < parameterDefinitions.length; i++) {
			ContainAnnotationParameterConfig containAnnotationParameterConfig = parameterDefinitions[i];
			ParameterFilterChain parameterFilterChain = new SimpleParameterParseFilterChain(parameterFilters);
			Object value = parameterFilterChain.doFilter(channel, parameterDefinitions[i]);
			if (value == null) {
				value = channel.getParameter(containAnnotationParameterConfig);
			}
			args[i] = value;
		}
		return args;
	}

	public static Object getBean(InstanceFactory instanceFactory, BeanDefinition beanDefinition, Channel channel,
			Constructor<?> constructor, Collection<ParameterFilter> parameterFilters) {
		try {
			return instanceFactory.getInstance(beanDefinition.getId(), constructor.getParameterTypes(),
					getParameterValues(channel, ParameterUtils.getParameterConfigs(constructor), parameterFilters));
		} catch (Throwable e) {
			throw new BeansException(beanDefinition.getId());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getRestPathParameterMap(Attributes attributes) {
		return (Map<String, String>) attributes.getAttribute(RESTURL_PATH_PARAMETER);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setRestPathParameterMap(Attributes attributes, Map<String, String> parameterMap) {
		attributes.setAttribute(RESTURL_PATH_PARAMETER, parameterMap);
	}

	public static boolean isRestPathParameterMapAttributeName(String name) {
		return RESTURL_PATH_PARAMETER.equals(name);
	}

	/**
	 * 判断是否是AJAX请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpRequest request) {
		return "XMLHttpRequest".equals(request.getHeader(HeadersConstants.X_REQUESTED_WITH));
	}

	/**
	 * 判断是否是json请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isJsonRequest(HttpRequest request) {
		return isDesignatedContentType(request, MimeTypeConstants.APPLICATION_JSON_VALUE);
	}

	public static boolean isFormRequest(HttpRequest request) {
		return isDesignatedContentType(request, MimeTypeConstants.APPLICATION_X_WWW_FORM_URLENCODED_VALUE);
	}

	public static boolean isMultipartRequest(HttpRequest request) {
		return isDesignatedContentType(request, MimeTypeConstants.MULTIPART_FORM_DATA_VALUE);
	}

	public static boolean isDesignatedContentType(HttpRequest request, String contentType) {
		return StringUtils.contains(request.getContentType(), contentType, true);
	}

	@SuppressWarnings("rawtypes")
	public static String getExistActionErrMsg(Action action, Action oldAction) {
		StringBuilder sb = new StringBuilder();
		sb.append("存在同样的controller[");
		sb.append(action.toString());
		sb.append("],原来的[");
		sb.append(oldAction.toString());
		sb.append("]");
		return sb.toString();
	}

	public static String getRPCPath(PropertyFactory propertyFactory) {
		String path = propertyFactory.getProperty("mvc.http.rpc-path");
		return StringUtils.isEmpty(path) ? "/rpc" : path;
	}

	public static LinkedList<ParameterFilter> getParameterFilters(InstanceFactory instanceFactory, Class<?> clz,
			Method method) {
		LinkedList<ParameterFilter> list = new LinkedList<ParameterFilter>();
		Controller controller = clz.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ParameterFilter> clazz : controller.parameterFilter()) {
				list.add(instanceFactory.getInstance(clazz));
			}
		}

		controller = method.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ParameterFilter> clazz : controller.parameterFilter()) {
				list.add(instanceFactory.getInstance(clazz));
			}
		}
		return list;
	}

	public static LinkedList<ParameterFilter> getParameterFilters(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		return getParameterFilters(instanceFactory, propertyFactory, "mvc.parameter.filters");
	}

	public static LinkedList<ParameterFilter> getParameterFilters(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, String key) {
		String[] filters = StringUtils.commonSplit(propertyFactory.getProperty(key));
		LinkedList<ParameterFilter> list = new LinkedList<ParameterFilter>();
		if (!ArrayUtils.isEmpty(filters)) {
			for (String name : filters) {
				list.add((ParameterFilter) instanceFactory.getInstance(name));
			}
		}
		return list;
	}

	public static LinkedList<ActionFilter> getActionFilters(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		LinkedList<ActionFilter> filters = new LinkedList<ActionFilter>();
		BeanUtils.appendBean(filters, instanceFactory, propertyFactory, ActionFilter.class, "mvc.filters");
		BeanUtils.appendBean(filters, instanceFactory, propertyFactory, ActionFilter.class, "mvc.action.filters");
		filters.add(new ResponseWrapperFilter(instanceFactory));
		return filters;
	}

	public static LinkedList<Filter> getFilters(InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<Filter> filters = new LinkedList<Filter>();
		BeanUtils.appendBean(filters, instanceFactory, propertyFactory, Filter.class, "mvc.filters");
		filters.add(getHttpActionServiceFilter(instanceFactory, propertyFactory));
		filters.add(new HttpNotFoundService());
		return filters;
	}

	public static String getCharsetName(PropertyFactory propertyFactory) {
		String charsetName = propertyFactory.getProperty("mvc.charsetName");
		return StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME : charsetName;
	}

	public static int getWarnExecuteTime(PropertyFactory propertyFactory) {
		return StringUtils.parseInt(propertyFactory.getProperty("mvc.warn-execute-time"), 100);
	}

	/**
	 * 获取ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIP(HttpRequest request) {
		String ip = request.getHeader(HeadersConstants.X_FORWARDED_FOR);
		return ip == null ? request.getRemoteAddr() : ip;
	}

	public static boolean isSupportCookieValue(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(propertyFactory.getProperty("mvc.parameter.cookie"));
	}

	// 默认开启跨域
	public static boolean isSupportCorssDomain(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(propertyFactory.getProperty("mvc.http.cross-domain"), true);
	}

	public static String getSourceRoot(PropertyFactory propertyFactory) {
		return propertyFactory.getProperty("mvc.http.resource.root");
	}

	public static String[] getSourcePath(PropertyFactory propertyFactory) {
		String arr = propertyFactory.getProperty("mvc.http.resource.path");
		if (StringUtils.isEmpty(arr)) {
			return null;
		}

		return StringUtils.commonSplit(arr);
	}

	public static String getHttpParameterActionKey(PropertyFactory propertyFactory) {
		String actionKey = propertyFactory.getProperty("mvc.http.actionKey");
		return StringUtils.isEmpty(actionKey) ? "action" : actionKey;
	}

	public static HttpActionServiceFilter getHttpActionServiceFilter(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		String packageName = propertyFactory.getProperty("mvc.http.scanning");
		packageName = StringUtils.isEmpty(packageName) ? "" : packageName;
		return instanceFactory.getInstance(HttpActionServiceFilter.class, instanceFactory, propertyFactory,
				ResourceUtils.getClassList(packageName));
	}

	public static JSONParseSupport getJsonParseSupport(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		JSONParseSupport jsonParseSupport;
		String jsonParseSupportBeanName = propertyFactory.getProperty("mvc.json");
		if (StringUtils.isEmpty(jsonParseSupportBeanName)) {
			jsonParseSupport = JSONUtils.DEFAULT_JSON_SUPPORT;
		} else {
			jsonParseSupport = instanceFactory.getInstance(jsonParseSupportBeanName);
		}
		return jsonParseSupport;
	}

	public static boolean isDebug(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(propertyFactory.getProperty("mvc.debug"), false);
	}

	public static Map<String, String> getRequestFirstValueParameters(HttpRequest request,
			KeyValuePairFilter<String, String> filter) {
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
					.filter(new SimpleKeyValuePair<String, String>(name, values[0]));
			if (keyValuePair == null) {
				continue;
			}

			map.put(keyValuePair.getKey(), keyValuePair.getValue());
		}
		return map;
	}

	public static MultiValueMap<String, String> getRequestParameters(HttpRequest request,
			KeyValuePairFilter<String, String[]> filter) {
		Map<String, String[]> requestParams = request.getParameterMap();
		if (requestParams == null || requestParams.isEmpty()) {
			return null;
		}

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>(requestParams.size(), 1);
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
						.filter(new SimpleKeyValuePair<String, String[]>(name, values));
				if (keyValuePair == null) {
					continue;
				}

				map.put(keyValuePair.getKey(), new LinkedList<String>(Arrays.asList(keyValuePair.getValue())));
			}
		}
		return map;
	}

	public static Map<String, String> getRequestParameterAndAppendValues(HttpRequest request,
			CharSequence appendValueChars, KeyValuePairFilter<String, String[]> filter) {
		if (filter == null) {
			Map<String, String[]> requestParams = request.getParameterMap();
			if (CollectionUtils.isEmpty(requestParams)) {
				return null;
			}

			Map<String, String> params = new HashMap<String, String>(requestParams.size(), 1);
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
			MultiValueMap<String, String> requestParams = getRequestParameters(request, filter);
			if (CollectionUtils.isEmpty(requestParams)) {
				return null;
			}

			Map<String, String> params = new HashMap<String, String>(requestParams.size(), 1);
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

	public static void responseCrossDomain(CrossDomainDefinition crossDomainDefinition, HttpResponse httpResponse) {
		/* 允许跨域的主机地址 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getOrigin())) {
			httpResponse.setHeader(HeadersConstants.ACCESS_CONTROL_ALLOW_ORIGIN, crossDomainDefinition.getOrigin());
		}

		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getMethods())) {
			httpResponse.setHeader(HeadersConstants.ACCESS_CONTROL_ALLOW_METHODS, crossDomainDefinition.getMethods());
		}

		/* 重新预检验跨域的缓存时间 (s) */
		if (crossDomainDefinition.getMaxAge() > 0) {
			httpResponse.setHeader(HeadersConstants.ACCESS_CONTROL_MAX_AGE, crossDomainDefinition.getMaxAge() + "");
		}

		/* 允许跨域的请求头 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getHeaders())) {
			httpResponse.setHeader(HeadersConstants.ACCESS_CONTROL_ALLOW_HEADERS, crossDomainDefinition.getHeaders());
		}

		/* 是否携带cookie */
		httpResponse.setHeader(HeadersConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS,
				crossDomainDefinition.isCredentials() + "");
	}

	public static String parseRedirect(HttpRequest httpRequest, String text, boolean ignoreCase) {
		if (text == null) {
			return null;
		}

		if (StringUtils.startsWith(text, REDIRECT_PREFIX, ignoreCase)) {
			String url = text.substring(REDIRECT_PREFIX.length());
			if (StringUtils.isEmpty(url) || url.equals("/")) {
				return httpRequest.getContextPath();
			} else if (url.startsWith("/")) {
				return httpRequest.getContextPath() + url;
			} else {
				return url;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends FilterInterface> LinkedList<T> getControllerFilter(Class<T> type, Class<?> clazz,
			Method method, InstanceFactory instanceFactory) {
		Filters filters = clazz.getAnnotation(Filters.class);
		LinkedList<T> list = new LinkedList<T>();
		if (filters != null) {
			for (Class<? extends FilterInterface> f : filters.value()) {
				if (type.isAssignableFrom(f)) {
					list.add((T) instanceFactory.getInstance(f));
				}
			}
		}

		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends FilterInterface> f : controller.filters()) {
				if (type.isAssignableFrom(f)) {
					list.add((T) instanceFactory.getInstance(f));
				}
			}
		}

		filters = method.getAnnotation(Filters.class);
		if (filters != null) {
			list.clear();
			for (Class<? extends FilterInterface> f : filters.value()) {
				if (type.isAssignableFrom(f)) {
					list.add((T) instanceFactory.getInstance(f));
				}
			}
		}

		controller = method.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends FilterInterface> f : controller.filters()) {
				if (type.isAssignableFrom(f)) {
					list.add((T) instanceFactory.getInstance(f));
				}
			}
		}
		return list;
	}

	public static void httpWrite(HttpChannel channel, String jsonp, JSONParseSupport jsonParseSupport, Object write)
			throws Throwable {
		if (write == null) {
			return;
		}

		if (write instanceof View) {
			((View) write).render(channel);
			return;
		}

		String callbackTag = null;
		if (scw.net.http.Method.GET.equals(channel.getRequest().getMethod())) {
			if (!StringUtils.isEmpty(jsonp)) {
				callbackTag = channel.getString(jsonp);
				if (StringUtils.isEmpty(callbackTag)) {
					callbackTag = null;
				}
			}
		}

		HttpResponse httpResponse = channel.getResponse();
		if (write instanceof String) {
			String redirect = parseRedirect(channel.getRequest(), (String) write, true);
			if (redirect != null) {
				httpResponse.sendRedirect(redirect);
				return;
			}
		}

		if (callbackTag != null) {
			httpResponse.setContentType(MimeTypeConstants.TEXT_JAVASCRIPT_VALUE);
			httpResponse.getWriter().write(callbackTag);
			httpResponse.getWriter().write(JSONP_RESP_PREFIX);
		}

		String content;
		if (write instanceof Text) {
			content = ((Text) write).getTextContent();
			if (callbackTag == null) {
				httpResponse.setContentType(((Text) write).getTextContentType());
			}
		} else if ((write instanceof String) || (ClassUtils.isPrimitiveOrWrapper(write.getClass()))) {
			content = write.toString();
		} else {
			content = jsonParseSupport.toJSONString(write);
		}

		if (callbackTag == null) {
			if (StringUtils.isEmpty(httpResponse.getContentType())) {
				httpResponse.setContentType(MimeTypeConstants.TEXT_HTML_VALUE);
			}
		}

		httpResponse.getWriter().write(content);

		if (callbackTag != null) {
			httpResponse.getWriter().write(JSONP_RESP_SUFFIX);
		}

		if (channel.isLogEnabled()) {
			channel.log(content);
		}
	}

	public static String getJsonp(PropertyFactory propertyFactory) {
		boolean enable = StringUtils.parseBoolean(propertyFactory.getProperty("mvc.http.jsonp.enable"), true);
		if (enable) {
			String jsonp = propertyFactory.getProperty("mvc.http.jsonp");
			return StringUtils.isEmpty(jsonp) ? "callback" : jsonp;
		}
		return null;
	}

	public static boolean isSupportHttpParameterAction(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(propertyFactory.getProperty("mvc.http.parameter.action.enable"), true);
	}

	public static RpcService getRpcService(PropertyFactory propertyFactory, InstanceFactory instanceFactory) {
		String beanName = propertyFactory.getProperty(RPC_SERVICE);
		if (StringUtils.isEmpty(beanName)) {
			if (instanceFactory.isInstance(RpcService.class) || instanceFactory.isSingleton(RpcService.class)) {
				return instanceFactory.getInstance(RpcService.class);
			}
		} else {
			if (instanceFactory.isInstance(beanName) && instanceFactory.isSingleton(beanName)) {
				return instanceFactory.getInstance(beanName);
			}
			logger.warn("RPC配置错误，无法实例化或不是一个单例: {}", beanName);
		}
		return null;
	}
}
