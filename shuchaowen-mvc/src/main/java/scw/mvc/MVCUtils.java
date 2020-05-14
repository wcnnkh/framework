package scw.mvc;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanUtils;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.parameter.DefaultParameterDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.lang.ParameterException;
import scw.mvc.action.Action;
import scw.mvc.http.cors.DefaultCorsConfigFactory;
import scw.net.http.HttpHeaders;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.cors.CorsConfig;
import scw.net.http.server.cors.CorsConfigFactory;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;
import scw.util.attribute.Attributes;
import scw.util.ip.IP;
import scw.value.property.PropertyFactory;

public final class MVCUtils implements MvcConstants {
	private static final String[] IP_HEADERS = GlobalPropertyFactory
			.getInstance().getValue("mvc.ip.headers", String[].class,
					new String[] { "X-Real-Ip", "X-Forwarded-For" });
	
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

	public static Object[] getParameterValues(Channel channel,
			ParameterDescriptor[] parameterDescriptors)
			throws ParameterException {
		Object[] args = new Object[parameterDescriptors.length];
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
			parameterDescriptor = new DefaultParameterDescriptor(ParameterUtils.getDisplayName(parameterDescriptor), parameterDescriptor);
			try {
				args[i] = channel.getParameter(parameterDescriptor);
			} catch (Exception e) {
				throw new ParameterException("Parameter error ["
						+ parameterDescriptor.getName() + "]", e);
			}
		}
		return args;
	}

	@SuppressWarnings({ "unchecked" })
	public static MultiValueMap<String, String> getRestfulParameterMap(
			Channel channel) {
		return (MultiValueMap<String, String>) channel
				.getAttribute(RESTURL_PATH_PARAMETER);
	}

	public static void setRestfulParameterMap(Channel channel,
			MultiValueMap<String, String> parameterMap) {
		channel.setAttribute(RESTURL_PATH_PARAMETER, parameterMap);
	}

	public static boolean isRestfulParameterMapAttributeName(String name) {
		return RESTURL_PATH_PARAMETER.equals(name);
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
	public static String getUntreatedIp(ServerHttpRequest serverHttpRequest) {
		for (String header : IP_HEADERS) {
			String ip = serverHttpRequest.getHeaders().getFirst(header);
			if (ip == null) {
				continue;
			}

			return ip;
		}
		return serverHttpRequest.getRemoteAddress().getHostName();
	}

	public static String getIP(ServerHttpRequest serverHttpRequest) {
		String ip = getUntreatedIp(serverHttpRequest);
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
			ServerHttpRequest request) {
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
			ServerHttpRequest request, CharSequence appendValueChars) {
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
			ServerHttpResponse serverHttpResponse) {
		/* 允许跨域的主机地址 */
		if (StringUtils.isNotEmpty(config.getOrigin())) {
			serverHttpResponse.getHeaders()
					.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
							config.getOrigin());
		}

		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		if (StringUtils.isNotEmpty(config.getMethods())) {
			serverHttpResponse.getHeaders().set(
					HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
					config.getMethods());
		}

		/* 重新预检验跨域的缓存时间 (s) */
		if (config.getMaxAge() > 0) {
			serverHttpResponse.getHeaders().set(HttpHeaders.ACCESS_CONTROL_MAX_AGE,
					config.getMaxAge() + "");
		}

		/* 允许跨域的请求头 */
		if (StringUtils.isNotEmpty(config.getHeaders())) {
			serverHttpResponse.getHeaders().set(
					HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
					config.getHeaders());
		}

		/* 是否携带cookie */
		serverHttpResponse.getHeaders().set(
				HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
				config.isCredentials() + "");
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

	/**
	 * 是否支持servlet
	 * 
	 * @return
	 */
	public static boolean isSupperServlet() {
		return SUPPORT_SERVLET;
	}

	public static JSONSupport getJsonSupport(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		JSONSupport jsonSupport;
		String jsonSupportBeanName = propertyFactory.getString("mvc.json");
		if (StringUtils.isEmpty(jsonSupportBeanName)) {
			jsonSupport = JSONUtils.getJsonSupport();
		} else {
			jsonSupport = instanceFactory.getInstance(jsonSupportBeanName);
		}
		return jsonSupport;
	}

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.mvc.package", String.class,
				BeanUtils.getScanAnnotationPackageName());
	}
}
