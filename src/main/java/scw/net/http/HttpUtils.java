package scw.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.StringUtils;
import scw.core.NestedRuntimeException;
import scw.net.Body;
import scw.net.NetworkUtils;
import scw.net.http.enums.Method;
import scw.net.http.request.BodyRequest;
import scw.net.http.request.FormRequest;
import scw.net.http.request.HttpRequest;

public final class HttpUtils {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private HttpUtils() {
	};

	public static String doGet(String url) {
		HttpRequest request = new HttpRequest(Method.GET, url);
		request.setRequestContentType("application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET.name());
		Body body = NetworkUtils.execute(request);
		return body.toString(DEFAULT_CHARSET);
	}

	public static String doPost(String url, Map<String, String> propertyMap, String body) {
		HttpRequest request = new BodyRequest(Method.POST, url, new Body(body, DEFAULT_CHARSET));
		request.setRequestProperties(propertyMap);
		Body response = NetworkUtils.execute(request);
		if (response == null) {
			return null;
		}

		return response.toString(DEFAULT_CHARSET);
	}

	public static String doPost(String url, Map<String, String> propertyMap, Map<String, ?> parameterMap,
			Charset charset) {
		FormRequest request = new FormRequest(Method.POST, url, charset.name());
		request.setRequestProperties(propertyMap);
		if (parameterMap != null) {
			for (Entry<String, ?> entry : parameterMap.entrySet()) {
				request.addParameter(entry.getKey(), entry.getValue());
			}
		}

		Body body = NetworkUtils.execute(request);
		return body.toString(charset);
	}

	public static String doPost(String url, Map<String, ?> parameterMap) {
		return doPost(url, null, parameterMap, DEFAULT_CHARSET);
	}

	public static String appendParameters(String prefix, Map<String, Object> paramMap, boolean encode,
			String charsetName) throws UnsupportedEncodingException {
		if (prefix == null || paramMap == null || paramMap.isEmpty()) {
			return prefix;
		}

		StringBuilder sb = new StringBuilder(128);
		sb.append(prefix);
		if (prefix != null || prefix.lastIndexOf("?") == -1) {
			sb.append("?");
		}

		Iterator<Entry<String, Object>> iterator = paramMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			if (StringUtils.isNull(entry.getKey()) || entry.getValue() == null) {
				continue;
			}

			sb.append(entry.getKey());
			sb.append("=");
			if (encode) {
				sb.append(URLEncoder.encode(entry.getValue().toString(), charsetName));
			} else {
				sb.append(entry.getValue());
			}
		}
		return sb.toString();
	}

	public static String appendParameters(String prefix, Map<String, Object> paramMap) {
		try {
			return appendParameters(prefix, paramMap, true, DEFAULT_CHARSET.name());
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
		}
	}

	public static String encode(Object value, String charsetName) {
		if (value == null) {
			return null;
		}

		try {
			return URLEncoder.encode(value.toString(), charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
		}
	}

	public static String encode(Object value) {
		return encode(value, DEFAULT_CHARSET.name());
	}

	public static String decode(String value, String charsetName) throws UnsupportedEncodingException {
		if (value == null) {
			return null;
		}

		return URLDecoder.decode(value, charsetName);
	}

	public static String decode(String value) {
		try {
			return decode(value, DEFAULT_CHARSET.name());
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public static String decode(String content, String charsetName, int count) throws UnsupportedEncodingException {
		if (count <= 0) {
			return content;
		}

		String newContent = content;
		for (int i = 0; i < count; i++) {
			newContent = decode(newContent, charsetName);
		}
		return newContent;
	}

	public static String encode(String content, String charsetName, int count) throws UnsupportedEncodingException {
		if (count <= 0) {
			return content;
		}

		String newContent = content;
		for (int i = 0; i < count; i++) {
			newContent = encode(newContent, charsetName);
		}
		return newContent;
	}

	public static final Map<String, String> paramsToMap(String params) {
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
