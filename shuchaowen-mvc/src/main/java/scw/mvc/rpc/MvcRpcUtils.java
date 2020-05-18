package scw.mvc.rpc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.context.Context;
import scw.core.annotation.Headers;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.NotSupportedException;
import scw.mvc.HttpChannel;
import scw.mvc.context.RequestContextManager;
import scw.net.message.Message;

public final class MvcRpcUtils {
	private MvcRpcUtils() {
	};

	private static ShareData privateGetShareData() {
		Context context = RequestContextManager.getInstance().getContext();
		if (context == null) {
			return null;
		}

		return (ShareData) context.getResource(MvcRpcUtils.class);
	}

	public static ShareData getShareData() {
		Context context = RequestContextManager.getInstance().getContext();
		if (context == null) {
			throw new NotSupportedException("不存在MVC的上下文");
		}

		ShareData shareData = (ShareData) context.getResource(MvcRpcUtils.class);
		if (shareData == null) {
			shareData = new ShareData();
			context.bindResource(MvcRpcUtils.class, shareData);
		}
		return shareData;
	}

	public static String getIP() {
		HttpChannel httpChannel = RequestContextManager.getCurrentChannel();
		if (httpChannel == null) {
			return null;
		}

		return httpChannel.getRequest().getIp();
	}

	public static Map<String, Object> getParameterMap(Method method, Object[] args, boolean appendMvcParameterMap) {
		String[] names = ParameterUtils.getParameterName(method);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (!ArrayUtils.isEmpty(names)) {
			for (int i = 0; i < names.length; i++) {
				map.put(names[i], args[i]);
			}
		}

		if (appendMvcParameterMap) {
			ShareData shareData = privateGetShareData();
			if (shareData != null) {
				map.putAll(shareData.getParameterMap());
			}
		}
		return map;
	}

	private static Map<String, String> getMessageHeaderMap(Message message, Collection<String> headerNames) {
		Map<String, String> map = new HashMap<String, String>();
		for (String name : headerNames) {
			String value = message.getHeaders().getFirst(name);
			if (StringUtils.isEmpty(value)) {
				continue;
			}

			map.put(name, value);
		}
		return map;
	}

	private static Map<String, String> getHeaderMap(Collection<String> headerNames) {
		if (CollectionUtils.isEmpty(headerNames)) {
			return null;
		}

		HttpChannel httpChannel = RequestContextManager.getCurrentChannel();
		if (httpChannel == null) {
			return null;
		}

		Map<String, String> headerMap = new HashMap<String, String>(headerNames.size());
		if (httpChannel.getRequest() instanceof Message) {
			headerMap.putAll(getMessageHeaderMap(httpChannel.getRequest(), headerNames));
		}
		return headerMap;
	}

	public static Map<String, String> getHeaderMap(String[] shareHeaderNames, Class<?> clazz, Method method) {
		Map<String, String> map = new HashMap<String, String>();
		if (shareHeaderNames != null) {
			Map<String, String> channelHeaderMap = getHeaderMap(Arrays.asList(shareHeaderNames));
			if (channelHeaderMap != null) {
				map.putAll(channelHeaderMap);
			}
		}

		Headers shareHeaders = method.getAnnotation(Headers.class);
		if (shareHeaders != null) {
			Map<String, String> channelHeaderMap = getHeaderMap(Arrays.asList(shareHeaders.value()));
			if (channelHeaderMap != null) {
				map.putAll(channelHeaderMap);
			}
		}

		ShareData shareData = privateGetShareData();
		if (shareData != null) {
			map.putAll(shareData.getHeaderMap());
		}
		return map;
	}
}
