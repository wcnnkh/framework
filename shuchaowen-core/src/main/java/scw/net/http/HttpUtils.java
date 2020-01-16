package scw.net.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Constants;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.json.JSONUtils;
import scw.lang.NotSupportException;
import scw.net.http.client.ClientHttpRequest;
import scw.net.http.client.ClientHttpRequestFactory;
import scw.net.http.client.HttpClient;
import scw.net.http.client.SimpleHttpClient;
import scw.util.ToMap;

public final class HttpUtils {
	private HttpUtils() {
	};

	public static final int DEFAULT_CONNECT_TIMEOUT = StringUtils
			.parseInt(SystemPropertyUtils.getProperty("scw.http.client.connect.timeout"), 10000);
	public static final int DEFAULT_READ_TIMEOUT = StringUtils
			.parseInt(SystemPropertyUtils.getProperty("scw.http.client.read.timeout"), 10000);
	
	private static final HttpClient HTTP_CLIENT = InstanceUtils.autoNewInstanceBySystemProperty(HttpClient.class,
			"scw.http.client", new SimpleHttpClient());

	public static HttpClient getHttpClient() {
		return HTTP_CLIENT;
	}

	public static ClientHttpRequest createRequest(String url, Method httpMethod, MediaType contentType,
			ClientHttpRequestFactory clientHttpRequestFactory) throws IOException {
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Could not get HttpURLConnection URI: " + e.getMessage(), e);
		}

		ClientHttpRequest request = clientHttpRequestFactory.createRequest(uri, httpMethod);
		request.setContentType(contentType);
		return request;
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

	public static String toFormBody(String key, Collection<?> values, String charsetName)
			throws UnsupportedEncodingException {
		if (StringUtils.isEmpty(key) || CollectionUtils.isEmpty(values)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (Object value : values) {
			if (value == null) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append("&");
			}

			sb.append(key);
			sb.append("=");
			if (StringUtils.isEmpty(charsetName)) {
				sb.append(URLEncoder.encode(value.toString(), charsetName));
			} else {
				sb.append(value.toString());
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String toFormBody(Map<String, ?> parameterMap, String charsetName)
			throws UnsupportedEncodingException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (Entry<String, ?> entry : parameterMap.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			String text;
			if (value instanceof Collection) {
				text = toFormBody(entry.getKey(), (Collection) value, charsetName);
			} else if (value.getClass().isArray()) {
				text = toFormBody(entry.getKey(), ArrayUtils.toList(value), charsetName);
			} else {
				text = toFormBody(entry.getKey(), Arrays.asList(value), charsetName);
			}

			if (text == null) {
				continue;
			}

			if (sb.length() != 0) {
				sb.append("&");
			}

			sb.append(text);
		}
		return sb.toString();
	}

	public static String appendParameters(String url, Map<String, ?> paramMap, String charsetName)
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

		String text = toFormBody(paramMap, charsetName);
		if (text != null) {
			sb.append(text);
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
