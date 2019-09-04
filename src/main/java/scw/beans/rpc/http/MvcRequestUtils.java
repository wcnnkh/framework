package scw.beans.rpc.http;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.beans.rpc.http.annotation.Http;
import scw.beans.rpc.http.annotation.HttpShareHeaders;
import scw.core.context.Context;
import scw.core.exception.NotSupportException;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;

public final class MvcRequestUtils {
	private MvcRequestUtils() {
	};

	public static RequestShareData getShareData() {
		Context context = MVCUtils.getContext();
		if (context == null) {
			throw new NotSupportException("不存在MVC的上下文");
		}

		RequestShareData shareData = (RequestShareData) context.getResource(MvcRequestUtils.class);
		if (shareData == null) {
			shareData = new RequestShareData();
			context.bindResource(MvcRequestUtils.class, shareData);
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
			RequestShareData shareData = getShareData();
			map.putAll(shareData.getParameterMap());
		}
		return map;
	}

	private static Map<String, String> getHeaderMap(Collection<String> headerNames) {
		Channel channel = MVCUtils.getContextChannel();
		if (channel == null) {
			return null;
		}

		if (channel instanceof HttpChannel) {
			HttpChannel httpChannel = (HttpChannel) channel;
			Map<String, String> map = new HashMap<String, String>();
			Enumeration<String> enumeration = httpChannel.getRequest().getHeaderNames();
			while (enumeration.hasMoreElements()) {
				String name = enumeration.nextElement();
				String value = httpChannel.getRequest().getHeader(name);
				if (value == null) {
					continue;
				}

				map.put(name, value);
			}
			return map;
		}

		return null;
	}

	public static Map<String, String> getHeaderMap(Class<?> clazz, Method method) {
		HttpShareHeaders httpShareHeaders = clazz.getAnnotation(HttpShareHeaders.class);
		String[] headers = null;
		if (httpShareHeaders != null) {
			headers = httpShareHeaders.value();
		}

		httpShareHeaders = method.getAnnotation(HttpShareHeaders.class);
		if (httpShareHeaders != null) {
			headers = httpShareHeaders.value();
		}

		Map<String, String> map = new HashMap<String, String>();
		if (headers != null) {
			Map<String, String> channelHeaderMap = getHeaderMap(Arrays.asList(headers));
			if (channelHeaderMap != null) {
				map.putAll(channelHeaderMap);
			}
		}

		Http http = method.getAnnotation(Http.class);
		if (http != null) {
			Map<String, String> channelHeaderMap = getHeaderMap(Arrays.asList(http.shareHeaders()));
			if (channelHeaderMap != null) {
				map.putAll(channelHeaderMap);
			}
		}

		Context context = MVCUtils.getContext();
		if (context != null) {
			RequestShareData shareData = (RequestShareData) context.getResource(MvcRequestUtils.class);
			if (shareData != null) {
				map.putAll(shareData.getHeaderMap());
			}
		}
		return map;
	}
}
