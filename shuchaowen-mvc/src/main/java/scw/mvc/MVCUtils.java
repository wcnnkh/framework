package scw.mvc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.parameter.ParameterConfig;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.lang.ParameterException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.action.AbstractAction;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFactory;
import scw.mvc.action.MultiActionFactory;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Filters;
import scw.mvc.context.ContextManager;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.cors.CorsConfig;
import scw.mvc.http.cors.CorsConfigFactory;
import scw.mvc.http.cors.DefaultCorsConfigFactory;
import scw.mvc.parameter.DefaultParameterFilterChain;
import scw.mvc.parameter.ParameterFilter;
import scw.mvc.parameter.ParameterFilterChain;
import scw.mvc.rpc.RpcService;
import scw.net.MimeTypeUtils;
import scw.net.http.HttpHeaders;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;
import scw.util.attribute.Attributes;
import scw.util.ip.IP;
import scw.util.value.property.PropertyFactory;

public final class MVCUtils implements MvcConstants {
	private static Logger logger = LoggerUtils.getLogger(MVCUtils.class);

	private static final String[] IP_HEADERS = SystemPropertyUtils
			.getArrayProperty(String.class, "mvc.ip.headers", new String[] {
					"X-Real-Ip", "X-Forwarded-For" });
	// 使用ip的模式 1表示使用第一个ip 2表示使用最后一个ip 其他表示原样返回
	private static final int USE_IP_MODEL = GlobalPropertyFactory.getInstance()
			.getValue("mvc.ip.model", int.class, 1);
	private static final boolean SUPPORT_SERVLET = ClassUtils
			.isPresent("javax.servlet.Servlet");

	private MVCUtils() {
	};

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

	public static Object[] getParameterValues(Channel channel,
			ParameterConfig[] parameterConfigs) {
		Action action = ContextManager.getCurrentAction();
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

	@SuppressWarnings({ "unchecked" })
	public static Map<String, String> getRestPathParameterMap(Channel channel) {
		return (Map<String, String>) channel
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
		String path = propertyFactory.getString("mvc.http.rpc-path");
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
		list.addAll(BeanUtils.getConfigurationList(ParameterFilter.class,
				instanceFactory));
		return list;
	}

	public static String getCharsetName(PropertyFactory propertyFactory) {
		String charsetName = propertyFactory.getString("mvc.charsetName");
		return StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME
				: charsetName;
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
			String ip = httpRequest.getHeaders().getFirst(header);
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

	// 默认开启跨域
	public static boolean isSupportCorssDomain(PropertyFactory propertyFactory) {
		return propertyFactory.getValue("mvc.http.cross-domain", boolean.class,
				true);
	}

	public static String getSourceRoot(PropertyFactory propertyFactory) {
		return propertyFactory.getString("mvc.http.resource.root");
	}

	public static String[] getResourcePaths(PropertyFactory propertyFactory) {
		String arr = propertyFactory.getString("mvc.http.resource.path");
		if (StringUtils.isEmpty(arr)) {
			return null;
		}

		return StringUtils.commonSplit(arr);
	}

	public static String getHttpParameterActionKey(
			PropertyFactory propertyFactory) {
		String actionKey = propertyFactory.getString("mvc.http.actionKey");
		return StringUtils.isEmpty(actionKey) ? "action" : actionKey;
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

	public static void responseCrossDomain(CorsConfig config,
			HttpResponse httpResponse) {
		/* 允许跨域的主机地址 */
		if (StringUtils.isNotEmpty(config.getOrigin())) {
			httpResponse.getHeaders()
					.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
							config.getOrigin());
		}

		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		if (StringUtils.isNotEmpty(config.getMethods())) {
			httpResponse.getHeaders().set(
					HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
					config.getMethods());
		}

		/* 重新预检验跨域的缓存时间 (s) */
		if (config.getMaxAge() > 0) {
			httpResponse.getHeaders().set(HttpHeaders.ACCESS_CONTROL_MAX_AGE,
					config.getMaxAge() + "");
		}

		/* 允许跨域的请求头 */
		if (StringUtils.isNotEmpty(config.getHeaders())) {
			httpResponse.getHeaders().set(
					HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
					config.getHeaders());
		}

		/* 是否携带cookie */
		httpResponse.getHeaders().set(
				HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
				config.isCredentials() + "");
	}

	public static LinkedList<ActionFilter> getActionFilters(Class<?> clazz,
			Method method, InstanceFactory instanceFactory) {
		Filters filters = clazz.getAnnotation(Filters.class);
		LinkedList<ActionFilter> list = new LinkedList<ActionFilter>();
		if (filters != null) {
			for (Class<? extends ActionFilter> f : filters.value()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}

		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ActionFilter> f : controller.filters()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}

		filters = method.getAnnotation(Filters.class);
		if (filters != null) {
			list.clear();
			for (Class<? extends ActionFilter> f : filters.value()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}

		controller = method.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ActionFilter> f : controller.filters()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
					list.add(instanceFactory.getInstance(f));
				}
			}
		}
		return list;
	}

	public static CorsConfigFactory getCorsConfigFactory(
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) {
		String beanName = propertyFactory.getString("mvc.cross-domain.factory");
		if (StringUtils.isEmpty(beanName)) {
			return instanceFactory.isInstance(CorsConfigFactory.class) ? instanceFactory
					.getInstance(CorsConfigFactory.class)
					: new DefaultCorsConfigFactory(propertyFactory);
		}
		return instanceFactory.getInstance(beanName);
	}

	public static RpcService getRpcService(PropertyFactory propertyFactory,
			InstanceFactory instanceFactory) {
		String beanName = propertyFactory.getString(RPC_SERVICE);
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

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.mvc.package", String.class,
				BeanUtils.getScanAnnotationPackageName());
	}

	public static JSONSupport getJsonSupport(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		JSONSupport jsonSupport;
		String jsonSupportBeanName = propertyFactory.getString("mvc.json");
		if (StringUtils.isEmpty(jsonSupportBeanName)) {
			jsonSupport = JSONUtils.DEFAULT_JSON_SUPPORT;
		} else {
			jsonSupport = instanceFactory.getInstance(jsonSupportBeanName);
		}
		return jsonSupport;
	}
}
