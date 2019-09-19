package scw.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.rpc.http.DefaultRpcService;
import scw.beans.rpc.http.RpcService;
import scw.core.AttributeManager;
import scw.core.Constants;
import scw.core.DefaultKeyValuePair;
import scw.core.Destroy;
import scw.core.KeyValuePair;
import scw.core.KeyValuePairFilter;
import scw.core.LinkedMultiValueMap;
import scw.core.MultiValueMap;
import scw.core.PropertyFactory;
import scw.core.ValueFactory;
import scw.core.annotation.ParameterName;
import scw.core.context.Context;
import scw.core.context.ContextManager;
import scw.core.context.support.ThreadLocalContextManager;
import scw.core.exception.BeansException;
import scw.core.exception.ParameterException;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.io.SerializerUtils;
import scw.io.serializer.Serializer;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Filters;
import scw.mvc.annotation.Methods;
import scw.mvc.annotation.Model;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.Text;
import scw.mvc.http.filter.CrossDomainDefinition;
import scw.mvc.http.filter.HttpServiceFilter;
import scw.net.ContentType;
import scw.result.exception.ResultExceptionFilter;

public final class MVCUtils {
	private static Logger logger = LoggerUtils.getLogger(MVCUtils.class);

	private MVCUtils() {
	};

	private static final ContextManager MVC_CONTEXT_MANAGER = new ThreadLocalContextManager(true);
	public static final String REDIRECT_PREFIX = "redirect:";
	private static final String RESTURL_PATH_PARAMETER = "_scw_resturl_path_parameter";
	public static final String ORIGIN_HEADER = "Access-Control-Allow-Origin";
	public static final String METHODS_HEADER = "Access-Control-Allow-Methods";
	public static final String MAX_AGE_HEADER = "Access-Control-Max-Age";
	public static final String HEADERS_HEADER = "Access-Control-Allow-Headers";
	public static final String CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
	private static final String JSONP_RESP_PREFIX = "(";
	private static final String JSONP_RESP_SUFFIX = ");";

	public static Channel getContextChannel() {
		Context context = MVC_CONTEXT_MANAGER.getCurrentContext();
		return (Channel) (context == null ? null : context.getResource(Channel.class));
	}

	public static Context getContext() {
		return MVC_CONTEXT_MANAGER.getCurrentContext();
	}

	public static void service(Collection<Filter> filters, Channel channel, int warnExecuteTime) throws Throwable {
		long t = System.currentTimeMillis();
		FilterChain filterChain = new SimpleFilterChain(filters);
		Context context = MVC_CONTEXT_MANAGER.createContext();
		context.bindResource(Channel.class, channel);
		try {
			channel.write(filterChain.doFilter(channel));
		} finally {
			try {
				if (channel instanceof Destroy) {
					((Destroy) channel).destroy();
				}
			} finally {
				MVC_CONTEXT_MANAGER.release(context);
				t = System.currentTimeMillis() - t;
				if (t > warnExecuteTime) {
					logger.warn("执行{}超时，用时{}ms", channel.toString(), t);
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
			throw new RuntimeException("鏋勯�燽ean澶辫触:" + type.getName(), e);
		}
	}

	public static void parameterWrapper(Object instance, ValueFactory<String> request, Class<?> type, String name) {
		try {
			privateParameterWrapper(instance, request, type,
					StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name : name + "."));
		} catch (Exception e) {
			throw new RuntimeException("鏋勯�燽ean澶辫触:" + type.getName(), e);
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

	public static ParameterDefinition[] getParameterDefinitions(Method method) {
		String[] names = ClassUtils.getParameterName(method);
		Annotation[][] parameterAnnoatations = method.getParameterAnnotations();
		Type[] parameterGenericTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		ParameterDefinition[] parameterDefinitions = new ParameterDefinition[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new SimpleParameterDefinition(names.length, names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i], i);
		}
		return parameterDefinitions;
	}

	public static ParameterDefinition[] getParameterDefinitions(Constructor<?> constructor) {
		String[] names = ClassUtils.getParameterName(constructor);
		Annotation[][] parameterAnnoatations = constructor.getParameterAnnotations();
		Type[] parameterGenericTypes = constructor.getGenericParameterTypes();
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		ParameterDefinition[] parameterDefinitions = new ParameterDefinition[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new SimpleParameterDefinition(names.length, names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i], i);
		}
		return parameterDefinitions;
	}

	public static Object[] getParameterValues(Channel channel, ParameterDefinition[] parameterDefinitions,
			Collection<ParameterFilter> parameterFilters) throws Throwable {
		Object[] args = new Object[parameterDefinitions.length];
		for (int i = 0; i < parameterDefinitions.length; i++) {
			ParameterDefinition parameterDefinition = parameterDefinitions[i];
			ParameterFilterChain parameterFilterChain = new SimpleParameterParseFilterChain(parameterFilters);
			Object value = parameterFilterChain.doFilter(channel, parameterDefinitions[i]);
			if (value == null) {
				value = channel.getParameter(parameterDefinition);
			}
			args[i] = value;
		}
		return args;
	}

	public static Object getBean(BeanFactory beanFactory, BeanDefinition beanDefinition, Channel channel,
			Constructor<?> constructor, Collection<ParameterFilter> parameterFilters) {
		try {
			return beanFactory.getInstance(beanDefinition.getId(), constructor.getParameterTypes(),
					getParameterValues(channel, getParameterDefinitions(constructor), parameterFilters));
		} catch (Throwable e) {
			throw new BeansException(beanDefinition.getId());
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getRestPathParameterMap(AttributeManager request) {
		return (Map<String, String>) request.getAttribute(RESTURL_PATH_PARAMETER);
	}

	public static void setRestPathParameterMap(AttributeManager attributeManager, Map<String, String> parameterMap) {
		attributeManager.setAttribute(RESTURL_PATH_PARAMETER, parameterMap);
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
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	/**
	 * 判断是否是json请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isJsonRequest(HttpRequest request) {
		return isDesignatedContentType(request, scw.net.ContentType.APPLICATION_JSON);
	}

	public static boolean isFormRequest(HttpRequest request) {
		return isDesignatedContentType(request, ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
	}

	public static boolean isMultipartRequest(HttpRequest request) {
		return isDesignatedContentType(request, ContentType.MULTIPART_FORM_DATA);
	}

	public static boolean isDesignatedContentType(HttpRequest request, String contentType) {
		return StringUtils.startsWithIgnoreCase(request.getContentType(), contentType);
	}

	public static scw.net.http.Method[] mergeRequestType(Class<?> clz, Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		if (clzController == null || methodController == null) {
			throw new ParameterException("方法或类上都不存在Controller注解");
		}

		Methods methods = method.getAnnotation(Methods.class);

		Map<String, scw.net.http.Method> requestTypeMap = new HashMap<String, scw.net.http.Method>();
		if (methods == null) {
			if (clzController != null) {
				for (scw.net.http.Method requestType : clzController.methods()) {
					requestTypeMap.put(requestType.name(), requestType);
				}
			}
		} else {
			for (scw.net.http.Method requestType : methods.value()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (methodController != null) {
			for (scw.net.http.Method requestType : methodController.methods()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (requestTypeMap.size() == 0) {
			requestTypeMap.put(scw.net.http.Method.GET.name(), scw.net.http.Method.GET);
		}

		return requestTypeMap.values().toArray(new scw.net.http.Method[0]);
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

	public static RpcService getRPCService(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		String rpcServerBeanName = propertyFactory.getProperty("mvc.http.rpc");
		if (StringUtils.isEmpty(rpcServerBeanName)) {
			String sign = propertyFactory.getProperty("mvc.http.rpc-sign");
			boolean enable = StringUtils.parseBoolean(propertyFactory.getProperty("mvc.http.rpc-enable"), false);
			if (enable || !StringUtils.isEmpty(sign)) {// 开启
				logger.info("rpc签名：{}", sign);
				String serializer = propertyFactory.getProperty("mvc.http.rpc-serializer");
				return beanFactory.getInstance(DefaultRpcService.class, beanFactory, sign,
						StringUtils.isEmpty(serializer) ? SerializerUtils.DEFAULT_SERIALIZER
								: (Serializer) beanFactory.getInstance(serializer));
			}
		} else {
			return beanFactory.getInstance(rpcServerBeanName);
		}

		return null;
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

	public static LinkedList<Filter> getFilters(InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<Filter> filters = new LinkedList<Filter>();
		if (instanceFactory.isInstance(ResultExceptionFilter.class)) {// 异常处理
			filters.add(instanceFactory.getInstance(ResultExceptionFilter.class));
		}

		BeanUtils.appendBean(filters, instanceFactory, propertyFactory, "mvc.filters");
		filters.add(getHttpServiceFilter(instanceFactory, propertyFactory));
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
		String ip = request.getHeader("x-forwarded-for");
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

	public static HttpServiceFilter getHttpServiceFilter(InstanceFactory beanFactory, PropertyFactory propertyFactory) {
		String actionKey = propertyFactory.getProperty("mvc.http.actionKey");
		actionKey = StringUtils.isEmpty(actionKey) ? "action" : actionKey;
		String packageName = propertyFactory.getProperty("mvc.http.scanning");
		packageName = StringUtils.isEmpty(packageName) ? "" : packageName;
		return beanFactory.getInstance(HttpServiceFilter.class, beanFactory, propertyFactory,
				ResourceUtils.getClassList(packageName), actionKey);
	}

	public static JSONParseSupport getJsonParseSupport(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		JSONParseSupport jsonParseSupport;
		String jsonParseSupportBeanName = propertyFactory.getProperty("mvc.json");
		if (StringUtils.isEmpty(jsonParseSupportBeanName)) {
			jsonParseSupport = JSONUtils.DEFAULT_JSON_SUPPORT;
		} else {
			jsonParseSupport = beanFactory.getInstance(jsonParseSupportBeanName);
		}
		return jsonParseSupport;
	}

	public static boolean isDebug(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(propertyFactory.getProperty("mvc.debug"), true);
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
					.filter(new DefaultKeyValuePair<String, String>(name, values[0]));
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
						.filter(new DefaultKeyValuePair<String, String[]>(name, values));
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
			httpResponse.setHeader(ORIGIN_HEADER, crossDomainDefinition.getOrigin());
		}

		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getMethods())) {
			httpResponse.setHeader(METHODS_HEADER, crossDomainDefinition.getMethods());
		}

		/* 重新预检验跨域的缓存时间 (s) */
		if (crossDomainDefinition.getMaxAge() > 0) {
			httpResponse.setHeader(MAX_AGE_HEADER, crossDomainDefinition.getMaxAge() + "");
		}

		/* 允许跨域的请求头 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getHeaders())) {
			httpResponse.setHeader(HEADERS_HEADER, crossDomainDefinition.getHeaders());
		}

		/* 是否携带cookie */
		httpResponse.setHeader(CREDENTIALS_HEADER, crossDomainDefinition.isCredentials() + "");
	}

	public static String parseRedirect(String text, boolean ignoreCase) {
		if (text == null) {
			return null;
		}

		if (StringUtils.startsWith(text, REDIRECT_PREFIX, ignoreCase)) {
			return text.substring(REDIRECT_PREFIX.length());
		}
		return null;
	}

	public static LinkedList<Filter> getControllerFilter(Class<?> clazz, Method method,
			InstanceFactory instanceFactory) {
		Filters filters = clazz.getAnnotation(Filters.class);
		LinkedList<Filter> list = new LinkedList<Filter>();
		if (filters != null) {
			for (Class<? extends Filter> f : filters.value()) {
				list.add(instanceFactory.getInstance(f));
			}
		}

		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends Filter> f : controller.filters()) {
				list.add(instanceFactory.getInstance(f));
			}
		}

		filters = method.getAnnotation(Filters.class);
		if (filters != null) {
			list.clear();
			for (Class<? extends Filter> f : filters.value()) {
				list.add(instanceFactory.getInstance(f));
			}
		}

		controller = method.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends Filter> f : controller.filters()) {
				list.add(instanceFactory.getInstance(f));
			}
		}
		return list;
	}

	public static void httpWrite(HttpChannel channel, String jsonp, JSONParseSupport jsonParseSupport, Object write,
			boolean checkView) throws Throwable {
		if (write == null) {
			return;
		}

		if (checkView && (write instanceof View)) {
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
			String redirect = parseRedirect((String) write, true);
			if (redirect != null) {
				httpResponse.sendRedirect(redirect);
				return;
			}
		}

		if (callbackTag != null) {
			httpResponse.setContentType(ContentType.TEXT_JAVASCRIPT);
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
				httpResponse.setContentType(ContentType.TEXT_HTML);
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
}
