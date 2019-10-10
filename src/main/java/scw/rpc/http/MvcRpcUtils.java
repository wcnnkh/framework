package scw.rpc.http;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.annotation.Headers;
import scw.core.context.Context;
import scw.core.exception.NotSupportException;
import scw.core.header.HeadersReadOnly;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.Request;
import scw.mvc.RequestResponseModel;

public final class MvcRpcUtils {
	private MvcRpcUtils() {
	};

	public static ShareData getShareData() {
		Context context = MVCUtils.getContext();
		if (context == null) {
			throw new NotSupportException("不存在MVC的上下文");
		}

		ShareData shareData = (ShareData) context.getResource(MvcRpcUtils.class);
		if (shareData == null) {
			shareData = new ShareData();
			context.bindResource(MvcRpcUtils.class, shareData);
		}
		return shareData;
	}

	public static Map<String, Object> getParameterMap(Method method, Object[] args, boolean appendMvcParameterMap) {
		String[] names = ClassUtils.getParameterName(method);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (!ArrayUtils.isEmpty(names)) {
			for (int i = 0; i < names.length; i++) {
				map.put(names[i], args[i]);
			}
		}

		if (appendMvcParameterMap) {
			ShareData shareData = getShareData();
			map.putAll(shareData.getParameterMap());
		}
		return map;
	}

	private static Map<String, String> getheaderMap(HeadersReadOnly headersReadOnly, Collection<String> headerNames) {
		Map<String, String> map = new HashMap<String, String>();
		for (String name : headerNames) {
			String value = headersReadOnly.getHeader(name);
			if (StringUtils.isEmpty(value)) {
				continue;
			}

			map.put(name, value);
		}
		return map;
	}

	@SuppressWarnings("rawtypes")
	private static Map<String, String> getHeaderMap(Collection<String> headerNames) {
		if (CollectionUtils.isEmpty(headerNames)) {
			return null;
		}

		Channel channel = MVCUtils.getContextChannel();
		if (channel == null) {
			return null;
		}

		Map<String, String> headerMap = new HashMap<String, String>(headerNames.size());
		if (channel instanceof RequestResponseModel) {
			Request request = ((RequestResponseModel) channel).getRequest();
			if (request instanceof HeadersReadOnly) {
				headerMap.putAll(getheaderMap((HeadersReadOnly) request, headerNames));
			}
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

		Context context = MVCUtils.getContext();
		if (context != null) {
			ShareData shareData = (ShareData) context.getResource(MvcRpcUtils.class);
			if (shareData != null) {
				map.putAll(shareData.getHeaderMap());
			}
		}
		return map;
	}
}
