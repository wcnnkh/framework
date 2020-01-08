package scw.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Constants;
import scw.core.string.StringCodecUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.io.ByteArray;
import scw.json.JSONUtils;
import scw.lang.NotSupportException;
import scw.net.Message;
import scw.net.NetworkUtils;
import scw.util.MimeType;
import scw.util.MimeTypeConstants;
import scw.util.ToMap;

public final class HttpUtils {
	private HttpUtils() {
	};

	public static String doGet(String url) {
		return doGet(url, Constants.DEFAULT_CHARSET_NAME);
	}

	public static String doGet(String url, String charsetName) {
		HttpRequest request = new HttpRequest(Method.GET, url);
		request.setContentType(new MimeType(MimeTypeConstants.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
		return execute(request, charsetName);
	}

	private static String execute(HttpRequest request, String charsetName) {
		Message message = NetworkUtils.execute(request);
		if (message == null) {
			return null;
		}

		return StringCodecUtils.getStringCodec(charsetName).decode(message.toByteArray());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String toJsonString(Object body) {
		if (body == null) {
			return null;
		}
		if (body instanceof String || TypeUtils.isPrimitiveOrWrapper(body.getClass())) {
			return body.toString();
		} else if (body instanceof ToMap) {
			return JSONUtils.toJSONString(XUtils.toMap((ToMap) body));
		} else {
			return JSONUtils.toJSONString(body);
		}
	}

	public static String postJson(String url, Map<String, String> requestProperties, Object body, String charsetName) {
		String text = toJsonString(body);
		HttpRequest request = new BodyRequest(Method.POST, url, text == null ? null : new ByteArray(text, charsetName));
		request.setContentType(new MimeType(MimeTypeConstants.APPLICATION_JSON, charsetName));
		request.setRequestProperties(requestProperties);
		return execute(request, charsetName);
	}

	public static String postJson(String url, Map<String, String> requestProperties, Object body) {
		return postJson(url, requestProperties, body, Constants.DEFAULT_CHARSET_NAME);
	}

	public static String postForm(String url, Map<String, String> requestProperties, Map<String, ?> parameterMap,
			String charsetName) {
		FormRequest request = new FormRequest(Method.POST, url, charsetName);
		request.setContentType(new MimeType(MimeTypeConstants.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
		request.setRequestProperties(requestProperties);
		request.addAll(parameterMap);
		return execute(request, charsetName);
	}

	public static String postForm(String url, Map<String, String> requestProperties,
			ToMap<String, ?> toRequestParameterMap, String charsetName) {
		return postForm(url, requestProperties, XUtils.toMap(toRequestParameterMap), charsetName);
	}

	public static String postForm(String url, Map<String, String> requestProperties, Map<String, ?> parameterMap) {
		return postForm(url, requestProperties, parameterMap, Constants.DEFAULT_CHARSET_NAME);
	}

	public static String appendParameters(String url, Map<String, Object> paramMap, String charsetName)
			throws UnsupportedEncodingException {
		if (paramMap == null || paramMap.isEmpty()) {
			return url;
		}

		StringBuilder sb = new StringBuilder(128);
		if (!StringUtils.isEmpty(url)) {
			sb.append(url);
			if (url.lastIndexOf("?") == -1) {
				sb.append("?");
			} else {
				sb.append("&");
			}
		}

		Iterator<Entry<String, Object>> iterator = paramMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			if (StringUtils.isNull(entry.getKey()) || entry.getValue() == null) {
				continue;
			}

			sb.append(entry.getKey());
			sb.append("=");
			if (StringUtils.isEmpty(charsetName)) {
				sb.append(URLEncoder.encode(entry.getValue().toString(), charsetName));
			} else {
				sb.append(entry.getValue());
			}

			if (iterator.hasNext()) {
				sb.append("&");
			}
		}
		return sb.toString();
	}

	public static String encode(Object value, String charsetName) {
		if (value == null) {
			return null;
		}

		try {
			return URLEncoder.encode(value.toString(), charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new NotSupportException(e);
		}
	}

	public static String encode(Object value) {
		return encode(value, Constants.DEFAULT_CHARSET_NAME);
	}

	public static String decode(String value, String charsetName) {
		if (value == null) {
			return null;
		}

		try {
			return URLDecoder.decode(value, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new NotSupportException(e);
		}
	}

	public static String decode(String value) {
		return decode(value, Constants.DEFAULT_CHARSET_NAME);
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

	public static String encode(Object content, String charsetName, int count) throws UnsupportedEncodingException {
		if (count <= 0 || content == null) {
			return content == null ? null : content.toString();
		}

		String newContent = content.toString();
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
