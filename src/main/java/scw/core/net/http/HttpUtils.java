package scw.core.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Constants;
import scw.core.exception.NestedRuntimeException;
import scw.core.io.ByteArray;
import scw.core.net.NetworkUtils;
import scw.core.net.http.enums.Method;
import scw.core.utils.StringUtils;

public final class HttpUtils {
	private HttpUtils() {
	};

	public static String doGet(String url) {
		HttpRequest request = new HttpRequest(Method.GET, url);
		request.setContentType(new DefaultContentType(ContentType.APPLICATION_X_WWW_FORM_URLENCODED, Constants.DEFAULT_CHARSET));
		ByteArray byteArray = NetworkUtils.execute(request);
		return byteArray.toString(Constants.DEFAULT_CHARSET);
	}

	public static String doPost(String url, Map<String, String> requestProperties, String body, String charsetName) {
		HttpRequest request = new BodyRequest(Method.POST, url, new ByteArray(body, charsetName));
		request.setRequestProperties(requestProperties);
		ByteArray responseBody = NetworkUtils.execute(request);
		if (responseBody == null) {
			return null;
		}

		return responseBody.toString(charsetName);
	}

	public static String doPost(String url, Map<String, String> requestProperties, String body) {
		return doPost(url, requestProperties, body, Constants.DEFAULT_CHARSET.name());
	}

	public static String doPost(String url, Map<String, String> requestProperties, Map<String, ?> parameterMap,
			String charsetName) {
		FormRequest request = new FormRequest(Method.POST, url, charsetName);
		request.setRequestProperties(requestProperties);
		request.addAll(parameterMap);
		ByteArray responseBody = NetworkUtils.execute(request);
		if (responseBody == null) {
			return null;
		}
		return responseBody.toString(charsetName);
	}

	public static String doPost(String url, Map<String, String> requestProperties, Map<String, ?> parameterMap) {
		return doPost(url, requestProperties, parameterMap, Constants.DEFAULT_CHARSET.name());
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
			return appendParameters(prefix, paramMap, true, Constants.DEFAULT_CHARSET.name());
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
		return encode(value, Constants.DEFAULT_CHARSET.name());
	}

	public static String decode(String value, String charsetName) throws UnsupportedEncodingException {
		if (value == null) {
			return null;
		}

		return URLDecoder.decode(value, charsetName);
	}

	public static String decode(String value) {
		try {
			return decode(value, Constants.DEFAULT_CHARSET.name());
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
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
