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
import java.util.Map;
import java.util.Map.Entry;

import scw.application.ApplicationConfigUtils;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.context.Context;
import scw.context.ContextManager;
import scw.context.DefaultThreadLocalContextManager;
import scw.context.Propagation;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.ValueFactory;
import scw.core.annotation.ParameterName;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterConfig;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.lang.ParameterException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Filters;
import scw.mvc.annotation.Model;
import scw.mvc.exception.ActionAppendExceptionHandler;
import scw.mvc.exception.DefaultExceptionHandler;
import scw.mvc.exception.ErrorAppendExceptionHandler;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.rpc.RpcService;
import scw.mvc.support.ActionFactory;
import scw.mvc.support.ActionFilter;
import scw.mvc.support.ActionServiceFilter;
import scw.mvc.support.CrossDomainDefinition;
import scw.mvc.support.CrossDomainDefinitionFactory;
import scw.mvc.support.HttpNotFoundFilter;
import scw.mvc.support.MultiActionFactory;
import scw.mvc.support.action.AbstractAction;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.Text;
import scw.net.http.HttpHeaders;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;
import scw.util.attribute.Attributes;
import scw.util.ip.IP;

public final class MVCUtils implements MvcConstants {
	private static Logger logger = LoggerUtils.getLogger(MVCUtils.class);
	private static final String[] IP_HEADERS = SystemPropertyUtils
			.getArrayProperty(String.class, "mvc.ip.headers", new String[] {
					"X-Real-Ip", "X-Forwarded-For" });
	// 使用ip的模式 1表示使用第一个ip 2表示使用最后一个ip 其他表示原样返回
	private static final int USE_IP_MODEL = StringUtils.parseInt(
			SystemPropertyUtils.getProperty("mvc.ip.model"), 1);
	private static final ContextManager<? extends Context> CONTEXT_MANAGER = new DefaultThreadLocalContextManager();
	private static final boolean SUPPORT_SERVLET = ClassUtils
			.isPresent("javax.servlet.Servlet");

	private MVCUtils() {
	};

	public static void service(Channel channel, Collection<Filter> filters,
			long warnExecuteTime, Collection<ExceptionHandler> exceptionHandlers) {
		MvcExecute execute = new MvcExecute(channel, filters, warnExecuteTime,
				exceptionHandlers);
		try {
			CONTEXT_MANAGER.execute(Propagation.REQUIRES_NEW, execute);
		} catch (Throwable e) {
			logger.error(e, channel.toString());
		}
	}

	public static Action getCurrentAction() {
		Context context = getContext();
		return (Action) (context == null ? null : context
				.getResource(Action.class));
	}

	public static Channel getCurrentChannel() {
		Context context = getContext();
		return (Channel) (context == null ? null : context
				.getResource(Channel.class));
	}

	public static Context getContext() {
		return CONTEXT_MANAGER.getContext();
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

	public static ActionFactory getActionFactory(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		MultiActionFactory multiActionFactory = new MultiActionFactory();
		LinkedList<ActionFactory> actionFactoryList = new LinkedList<ActionFactory>();
		BeanUtils.appendBean(actionFactoryList, beanFactory, propertyFactory,
				ActionFactory.class, "mvc.action.factory");
		if (!CollectionUtils.isEmpty(actionFactoryList)) {
			multiActionFactory.addAll(actionFactoryList);
		}

		multiActionFactory.add(beanFactory.getInstance(ActionFactory.class));
		return multiActionFactory;
	}

	public static Collection<ExceptionHandler> getExceptionHandlers(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<ExceptionHandler> exceptionHandlers = new LinkedList<ExceptionHandler>();
		BeanUtils.appendBean(exceptionHandlers, instanceFactory,
				propertyFactory, ExceptionHandler.class,
				"mvc.exception.handler");
		if (instanceFactory.isInstance(ExceptionHandler.class)
				&& instanceFactory.isSingleton(ExceptionHandler.class)) {
			exceptionHandlers.add(instanceFactory
					.getInstance(ExceptionHandler.class));
		}

		exceptionHandlers.add(instanceFactory
				.getInstance(ActionAppendExceptionHandler.class));
		exceptionHandlers.add(instanceFactory
				.getInstance(ErrorAppendExceptionHandler.class));
		if (instanceFactory.isInstance(DefaultExceptionHandler.class)) {
			exceptionHandlers.add(instanceFactory
					.getInstance(DefaultExceptionHandler.class));
		}
		return exceptionHandlers;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getParameterWrapper(ValueFactory<String> request,
			Class<T> type, String name) {
		try {
			return (T) privateParameterWrapper(request, type,
					StringUtils.isEmpty(name) ? null
							: (name.endsWith(".") ? name : name + "."));
		} catch (Exception e) {
			throw new RuntimeException("参数错误:" + type.getName(), e);
		}
	}

	public static void parameterWrapper(Object instance,
			ValueFactory<String> request, Class<?> type, String name) {
		try {
			privateParameterWrapper(instance, request, type,
					StringUtils.isEmpty(name) ? null
							: (name.endsWith(".") ? name : name + "."));
		} catch (Exception e) {
			throw new RuntimeException("参数错误:" + type.getName(), e);
		}
	}

	private static void privateParameterWrapper(Object instance,
			ValueFactory<String> request, Class<?> type, String prefix)
			throws Exception {
		Class<?> clz = type;
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())
						|| Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				ReflectionUtils.setAccessibleField(field);
				if (!field.getType().isPrimitive()
						&& field.get(instance) != null) {
					continue;
				}

				String fieldName = field.getName();
				ParameterName parameterName = field
						.getAnnotation(ParameterName.class);
				if (parameterName != null
						&& StringUtils.isNotEmpty(parameterName.value())) {
					fieldName = parameterName.value();
				}

				String key = StringUtils.isEmpty(prefix) ? fieldName : prefix
						+ fieldName;
				if (String.class.isAssignableFrom(field.getType())
						|| ClassUtils.isPrimitiveOrWrapper(field.getType())) {
					// 濡傛灉鏄熀鏈暟鎹被鍨�
					Object v = XUtils.getValue(request, key, field.getType());
					if (v != null) {
						ReflectionUtils.setFieldValue(clz, field, instance, v);
					}
				} else {
					ReflectionUtils.setFieldValue(
							clz,
							field,
							instance,
							privateParameterWrapper(request, field.getType(),
									key + "."));
				}
			}
			clz = clz.getSuperclass();
		}
	}

	private static Object privateParameterWrapper(ValueFactory<String> request,
			Class<?> type, String prefix) throws Exception {
		if (!ReflectionUtils.isInstance(type)) {
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

	public static Object[] getParameterValues(Channel channel,
			ParameterConfig[] parameterConfigs) {
		Action action = getCurrentAction();
		if (action != null && action instanceof AbstractAction) {
			return ((AbstractAction) action).getArgs(parameterConfigs, channel);
		}
		return getParameterValues(channel, parameterConfigs, null, null);
	}

	public static Object getParameterValue(Channel channel,
			ParameterConfig parameterConfig,
			Collection<ParameterFilter> parameterFilters,
			ParameterFilterChain chain) {
		ParameterFilterChain parameterFilterChain = new DefaultParameterFilterChain(
				parameterFilters, chain);
		try {
			return parameterFilterChain.doFilter(channel, parameterConfig);
		} catch (Throwable e) {
			if (ParameterException.class.isInstance(e)) {
				throw (ParameterException) e;
			}
			throw new ParameterException("Parameter error ["
					+ parameterConfig.getName() + "]", e);
		}
	}

	public static Object[] getParameterValues(Channel channel,
			ParameterConfig[] parameterConfigs,
			Collection<ParameterFilter> parameterFilters,
			ParameterFilterChain chain) throws ParameterException {
		Object[] args = new Object[parameterConfigs.length];
		for (int i = 0; i < parameterConfigs.length; i++) {
			args[i] = getParameterValue(channel, parameterConfigs[i],
					parameterFilters, chain);
		}
		return args;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getRestPathParameterMap(
			Attributes attributes) {
		return (Map<String, String>) attributes
				.getAttribute(RESTURL_PATH_PARAMETER);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setRestPathParameterMap(Attributes attributes,
			Map<String, String> parameterMap) {
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
		return "XMLHttpRequest".equals(request
				.getHeader(HttpHeaders.X_REQUESTED_WITH));
	}

	/**
	 * 判断是否是json请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isJsonRequest(Request request) {
		return isDesignatedContentType(request,
				MimeTypeUtils.APPLICATION_JSON_VALUE)
				|| isDesignatedContentType(request,
						MimeTypeUtils.TEXT_JSON_VALUE);
	}

	public static boolean isXmlRequeset(Request request) {
		return isDesignatedContentType(request,
				MimeTypeUtils.APPLICATION_XML_VALUE)
				|| isDesignatedContentType(request,
						MimeTypeUtils.TEXT_XML_VALUE);
	}

	public static boolean isFormRequest(Request request) {
		return isDesignatedContentType(request,
				MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED_VALUE);
	}

	public static boolean isMultipartRequest(Request request) {
		return isDesignatedContentType(request,
				MimeTypeUtils.MULTIPART_FORM_DATA_VALUE);
	}

	public static boolean isDesignatedContentType(Request request,
			String contentType) {
		return StringUtils.contains(request.getRawContentType(), contentType,
				true);
	}

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

	public static LinkedList<ParameterFilter> getParameterFilters(
			InstanceFactory instanceFactory, Class<?> clz, Method method) {
		LinkedList<ParameterFilter> list = new LinkedList<ParameterFilter>();
		Controller controller = clz.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ParameterFilter> clazz : controller
					.parameterFilter()) {
				list.add(instanceFactory.getInstance(clazz));
			}
		}

		controller = method.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ParameterFilter> clazz : controller
					.parameterFilter()) {
				list.add(instanceFactory.getInstance(clazz));
			}
		}

		if (instanceFactory.isInstance(ParameterFilter.class)) {
			list.add(instanceFactory.getInstance(ParameterFilter.class));
		}
		return list;
	}

	public static LinkedList<ParameterFilter> getParameterFilters(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<ParameterFilter> list = new LinkedList<ParameterFilter>();
		BeanUtils.appendBean(list, instanceFactory, propertyFactory,
				ParameterFilter.class, "mvc.parameter.filter");
		list.addAll(BeanUtils.getConfigurationList(ParameterFilter.class, null,
				instanceFactory, propertyFactory));
		return list;
	}

	public static LinkedList<Filter> getFilters(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<Filter> filters = new LinkedList<Filter>();
		BeanUtils.appendBean(filters, instanceFactory, propertyFactory,
				Filter.class, "mvc.filters");
		filters.add(instanceFactory.getInstance(ActionServiceFilter.class));
		return filters;
	}

	public static LinkedList<Filter> getActionFilter(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<Filter> filters = new LinkedList<Filter>();
		BeanUtils.appendBean(filters, instanceFactory, propertyFactory,
				Filter.class, "mvc.action.filters");
		filters.addAll(BeanUtils.getConfigurationList(ActionFilter.class, null,
				instanceFactory, propertyFactory));
		return filters;
	}

	public static LinkedList<Filter> getNotFoundFilters(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		LinkedList<Filter> filters = new LinkedList<Filter>();
		BeanUtils.appendBean(filters, instanceFactory, propertyFactory,
				Filter.class, "mvc.notfound.filters");
		filters.add(instanceFactory.getInstance(HttpNotFoundFilter.class));
		return filters;
	}

	public static String getCharsetName(PropertyFactory propertyFactory) {
		String charsetName = propertyFactory.getProperty("mvc.charsetName");
		return StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME
				: charsetName;
	}

	public static long getWarnExecuteTime(PropertyFactory propertyFactory) {
		return StringUtils.parseLong(
				propertyFactory.getProperty("mvc.warn-execute-time"), 100);
	}

	public static String getIP(Channel channel) {
		if (channel instanceof IP) {
			return ((IP) channel).getIP();
		}

		if (channel.getRequest() instanceof IP) {
			return ((IP) channel.getRequest()).getIP();
		}

		return null;
	}

	/**
	 * 获取未经处理的ip
	 * 
	 * @param headersReadOnly
	 * @param request
	 * @return
	 */
	public static String getUntreatedIp(HttpRequest httpRequest) {
		for (String header : IP_HEADERS) {
			String ip = httpRequest.getHeader(header);
			if (ip == null) {
				continue;
			}

			return ip;
		}
		return httpRequest.getRemoteAddr();
	}

	public static String getIP(HttpRequest httpRequest) {
		String ip = getUntreatedIp(httpRequest);
		if (USE_IP_MODEL == 1) {// 使用第一个
			String[] ipArray = StringUtils.commonSplit(ip);
			if (ArrayUtils.isEmpty(ipArray)) {
				return null;
			}

			return ipArray[0];
		} else if (USE_IP_MODEL == 2) {// 使用最后一个
			String[] ipArray = StringUtils.commonSplit(ip);
			if (ArrayUtils.isEmpty(ipArray)) {
				return null;
			}

			return ipArray[ipArray.length - 1];
		}
		return ip;
	}

	public static boolean isSupportCookieValue(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(propertyFactory
				.getProperty("mvc.parameter.cookie"));
	}

	// 默认开启跨域
	public static boolean isSupportCorssDomain(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(
				propertyFactory.getProperty("mvc.http.cross-domain"), true);
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

	public static String getHttpParameterActionKey(
			PropertyFactory propertyFactory) {
		String actionKey = propertyFactory.getProperty("mvc.http.actionKey");
		return StringUtils.isEmpty(actionKey) ? "action" : actionKey;
	}

	public static JSONSupport getJsonParseSupport(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		JSONSupport jsonParseSupport;
		String jsonParseSupportBeanName = propertyFactory
				.getProperty("mvc.json");
		if (StringUtils.isEmpty(jsonParseSupportBeanName)) {
			jsonParseSupport = JSONUtils.DEFAULT_JSON_SUPPORT;
		} else {
			jsonParseSupport = instanceFactory
					.getInstance(jsonParseSupportBeanName);
		}
		return jsonParseSupport;
	}

	public static MultiValueMap<String, String> getRequestParameters(
			HttpRequest request) {
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

			map.put(name, new LinkedList<String>(Arrays.asList(values)));
		}
		return map;
	}

	public static Map<String, String> getRequestParameterAndAppendValues(
			HttpRequest request, CharSequence appendValueChars) {
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
	}

	public static void responseCrossDomain(
			CrossDomainDefinition crossDomainDefinition,
			HttpResponse httpResponse) {
		/* 允许跨域的主机地址 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getOrigin())) {
			httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
					crossDomainDefinition.getOrigin());
		}

		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getMethods())) {
			httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
					crossDomainDefinition.getMethods());
		}

		/* 重新预检验跨域的缓存时间 (s) */
		if (crossDomainDefinition.getMaxAge() > 0) {
			httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE,
					crossDomainDefinition.getMaxAge() + "");
		}

		/* 允许跨域的请求头 */
		if (StringUtils.isNotEmpty(crossDomainDefinition.getHeaders())) {
			httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
					crossDomainDefinition.getHeaders());
		}

		/* 是否携带cookie */
		httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
				crossDomainDefinition.isCredentials() + "");
	}

	public static String parseRedirect(HttpRequest httpRequest, String text,
			boolean ignoreCase) {
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

	public static LinkedList<Filter> getControllerFilter(Class<?> clazz,
			Method method, InstanceFactory instanceFactory) {
		Filters filters = clazz.getAnnotation(Filters.class);
		LinkedList<Filter> list = new LinkedList<Filter>();
		if (filters != null) {
			for (Class<? extends Filter> f : filters.value()) {
				if (Filter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}

		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends Filter> f : controller.filters()) {
				if (Filter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}

		filters = method.getAnnotation(Filters.class);
		if (filters != null) {
			list.clear();
			for (Class<? extends Filter> f : filters.value()) {
				if (Filter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}

		controller = method.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends Filter> f : controller.filters()) {
				if (Filter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}
		return list;
	}

	public static void httpWrite(HttpChannel channel, String jsonp,
			JSONSupport jsonParseSupport, Object write) throws Throwable {
		if (write == null) {
			return;
		}

		if (write instanceof View) {
			((View) write).render(channel);
			return;
		}

		String callbackTag = null;
		if (scw.net.http.Method.GET == channel.getRequest().getMethod()) {
			if (!StringUtils.isEmpty(jsonp)) {
				callbackTag = channel.getString(jsonp);
				if (StringUtils.isEmpty(callbackTag)) {
					callbackTag = null;
				}
			}
		}

		HttpResponse httpResponse = channel.getResponse();
		if (write instanceof String) {
			String redirect = parseRedirect(channel.getRequest(),
					(String) write, true);
			if (redirect != null) {
				httpResponse.sendRedirect(redirect);
				return;
			}
		}

		if (callbackTag != null) {
			httpResponse.setMimeType(MimeTypeUtils.TEXT_JAVASCRIPT);
			httpResponse.getWriter().write(callbackTag);
			httpResponse.getWriter().write(JSONP_RESP_PREFIX);
		}

		String content;
		if (write instanceof Text) {
			content = ((Text) write).getTextContent();
			if (callbackTag == null) {
				MimeType mimeType = ((Text) write).getMimeType();
				if (mimeType != null) {
					httpResponse.setMimeType(mimeType);
				}
			}
		} else if ((write instanceof String)
				|| (ClassUtils.isPrimitiveOrWrapper(write.getClass()))) {
			content = write.toString();
		} else {
			content = jsonParseSupport.toJSONString(write);
		}

		if (callbackTag == null) {
			if (StringUtils.isEmpty(httpResponse.getContentType())) {
				httpResponse.setMimeType(MimeTypeUtils.TEXT_HTML);
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
		boolean enable = StringUtils.parseBoolean(
				propertyFactory.getProperty("mvc.http.jsonp.enable"), true);
		if (enable) {
			String jsonp = propertyFactory.getProperty("mvc.http.jsonp");
			return StringUtils.isEmpty(jsonp) ? "callback" : jsonp;
		}
		return null;
	}

	public static CrossDomainDefinitionFactory getCrossDomainDefinitionFactory(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		String beanName = propertyFactory
				.getProperty("mvc.cross-domain.factory");
		if (StringUtils.isEmpty(beanName)) {
			return instanceFactory
					.isInstance(CrossDomainDefinitionFactory.class) ? instanceFactory
					.getInstance(CrossDomainDefinitionFactory.class) : null;
		}
		return instanceFactory.getInstance(beanName);
	}

	public static RpcService getRpcService(PropertyFactory propertyFactory,
			InstanceFactory instanceFactory) {
		String beanName = propertyFactory.getProperty(RPC_SERVICE);
		if (StringUtils.isEmpty(beanName)) {
			if (instanceFactory.isInstance(RpcService.class)
					|| instanceFactory.isSingleton(RpcService.class)) {
				return instanceFactory.getInstance(RpcService.class);
			}
		} else {
			if (instanceFactory.isInstance(beanName)
					&& instanceFactory.isSingleton(beanName)) {
				return instanceFactory.getInstance(beanName);
			}
			logger.warn("RPC配置错误，无法实例化或不是一个单例: {}", beanName);
		}
		return null;
	}

	/**
	 * 是否支持servlet
	 * 
	 * @return
	 */
	public static boolean isSupperServlet() {
		return SUPPORT_SERVLET;
	}

	public static String getAnnotationScannPackage(
			PropertyFactory propertyFactory) {
		return ApplicationConfigUtils.getPackageName(propertyFactory,
				"mvc.annotation.scann");
	}
}
