package scw.net.http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.core.Constants;
import scw.core.exception.NestedRuntimeException;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.ByteArray;
import scw.json.JSONUtils;
import scw.net.ContentType;
import scw.net.DefaultContentType;
import scw.net.NetworkUtils;

public final class HttpUtils {
	private HttpUtils() {
	};

	public static String doGet(String url) {
		return doGet(url, Constants.DEFAULT_CHARSET_NAME);
	}

	public static String doGet(String url, String charsetName) {
		HttpRequest request = new HttpRequest(Method.GET, url);
		request.setContentType(new DefaultContentType(
				ContentType.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
		ByteArray byteArray = NetworkUtils.execute(request);
		return byteArray.toString(charsetName);
	}

	public static String postJson(String url,
			Map<String, String> requestProperties, Object body,
			String charsetName) {
		String text = null;
		if (body != null) {
			if (body instanceof String) {
				text = body.toString();
			} else if (body instanceof ToParameterMap) {
				text = JSONUtils
						.toJSONString(toParameterMap((ToParameterMap) body));
			}
		}
		HttpRequest request = new BodyRequest(Method.POST, url, new ByteArray(
				text, charsetName));
		request.setContentType(new DefaultContentType(
				ContentType.APPLICATION_JSON, charsetName));
		request.setRequestProperties(requestProperties);
		ByteArray responseBody = NetworkUtils.execute(request);
		if (responseBody == null) {
			return null;
		}

		return responseBody.toString(charsetName);
	}

	public static String postJson(String url,
			Map<String, String> requestProperties, Object body) {
		return postJson(url, requestProperties, body,
				Constants.DEFAULT_CHARSET_NAME);
	}

	public static Map<String, Object> toParameterMap(
			ToParameterMap toRequestParameterMap) {
		if (toRequestParameterMap == null) {
			return null;
		}

		Map<String, Object> map = toRequestParameterMap.toRequestParameterMap();
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}

		for (Entry<String, Object> entry : map.entrySet()) {
			entry.setValue(toParameterMapTransformation(entry.getValue()));
		}
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object toParameterMapTransformation(Object value) {
		if (value == null) {
			return value;
		}

		if (value instanceof ToParameterMap) {
			return toParameterMap((ToParameterMap) value);
		} else if (value instanceof Collection) {
			Collection list = (Collection) value;
			if (CollectionUtils.isEmpty(list)) {
				return value;
			}

			List<Object> newList = new ArrayList<Object>(list.size());
			for (Object v : list) {
				Object tmp = toParameterMapTransformation(v);
				if (tmp == null) {
					continue;
				}
				newList.add(tmp);
			}
			return newList;
		} else if (value instanceof Map) {
			Map map = (Map) value;
			if (CollectionUtils.isEmpty(map)) {
				return value;
			}

			Set<Map.Entry> set = map.entrySet();
			for (Map.Entry entry : set) {
				entry.setValue(toParameterMapTransformation(entry.getValue()));
			}
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			if (len == 0) {
				return value;
			}

			for (int i = 0; i < len; i++) {
				Object v = Array.get(value, i);
				Array.set(value, i, toParameterMapTransformation(v));
			}
		}
		return value;
	}

	public static String postForm(String url,
			Map<String, String> requestProperties, Map<String, ?> parameterMap,
			String charsetName) {
		FormRequest request = new FormRequest(Method.POST, url, charsetName);
		request.setContentType(new DefaultContentType(
				ContentType.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
		request.setRequestProperties(requestProperties);
		request.addAll(parameterMap);
		ByteArray responseBody = NetworkUtils.execute(request);
		if (responseBody == null) {
			return null;
		}
		return responseBody.toString(charsetName);
	}

	public static String postForm(String url,
			Map<String, String> requestProperties,
			ToParameterMap toRequestParameterMap, String charsetName) {
		return postForm(url, requestProperties,
				toParameterMap(toRequestParameterMap), charsetName);
	}

	public static String postForm(String url,
			Map<String, String> requestProperties, Map<String, ?> parameterMap) {
		return postForm(url, requestProperties, parameterMap,
				Constants.DEFAULT_CHARSET_NAME);
	}

	public static String appendParameters(String prefix,
			Map<String, Object> paramMap, boolean encode, String charsetName)
			throws UnsupportedEncodingException {
		if (prefix == null || paramMap == null || paramMap.isEmpty()) {
			return prefix;
		}

		StringBuilder sb = new StringBuilder(128);
		sb.append(prefix);
		if (prefix != null || prefix.lastIndexOf("?") == -1) {
			sb.append("?");
		}

		Iterator<Entry<String, Object>> iterator = paramMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			if (StringUtils.isNull(entry.getKey()) || entry.getValue() == null) {
				continue;
			}

			sb.append(entry.getKey());
			sb.append("=");
			if (encode) {
				sb.append(URLEncoder.encode(entry.getValue().toString(),
						charsetName));
			} else {
				sb.append(entry.getValue());
			}
		}
		return sb.toString();
	}

	public static String appendParameters(String prefix,
			Map<String, Object> paramMap) {
		try {
			return appendParameters(prefix, paramMap, true,
					Constants.DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String encode(Object value, String charsetName) {
		if (value == null) {
			return null;
		}

		try {
			return URLEncoder.encode(value.toString(), charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String encode(Object value) {
		return encode(value, Constants.DEFAULT_CHARSET_NAME);
	}

	public static String decode(String value, String charsetName)
			throws UnsupportedEncodingException {
		if (value == null) {
			return null;
		}

		return URLDecoder.decode(value, charsetName);
	}

	public static String decode(String value) {
		try {
			return decode(value, Constants.DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
		}
	}

	public static String decode(String content, String charsetName, int count)
			throws UnsupportedEncodingException {
		if (count <= 0) {
			return content;
		}

		String newContent = content;
		for (int i = 0; i < count; i++) {
			newContent = decode(newContent, charsetName);
		}
		return newContent;
	}

	public static String encode(String content, String charsetName, int count)
			throws UnsupportedEncodingException {
		if (count <= 0) {
			return content;
		}

		String newContent = content;
		for (int i = 0; i < count; i++) {
			newContent = encode(newContent, charsetName);
		}
		return newContent;
	}

	public static Map<String, String> paramsToMap(String params) {
		Map<String, String> map = new Hashtable<String, String>();
		if (params != null) {
			String[] strs = params.split("&");
			for (String str : strs) {
				String[] temp = str.split("=");
				if (temp.length == 2) {
					map.put(temp[0], temp[1]);
				}
			}
		}
		return map;
	}
}
