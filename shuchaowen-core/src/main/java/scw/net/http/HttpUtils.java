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

import scw.core.Assert;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.json.JSONSupport;
import scw.lang.NotSupportedException;
import scw.net.http.client.ClientHttpRequest;
import scw.net.http.client.ClientHttpRequestFactory;
import scw.net.http.client.HttpClient;
import scw.net.uri.UriComponents;
import scw.net.uri.UriComponentsBuilder;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;
import scw.util.ToMap;

public final class HttpUtils {
	private HttpUtils() {
	};

	public static final int DEFAULT_CONNECT_TIMEOUT = StringUtils
			.parseInt(GlobalPropertyFactory.getInstance().getString("scw.http.client.connect.timeout"), 10000);
	public static final int DEFAULT_READ_TIMEOUT = StringUtils
			.parseInt(GlobalPropertyFactory.getInstance().getString("scw.http.client.read.timeout"), 10000);

	private static final HttpClient HTTP_CLIENT = InstanceUtils.getSystemConfiguration(HttpClient.class);

	public static HttpClient getHttpClient() {
		return HTTP_CLIENT;
	}

	public static ClientHttpRequest createRequest(String url, HttpMethod httpMethod, MediaType contentType,
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
	public static String toJsonString(Object body, JSONSupport jsonSupport) {
		if (body == null) {
			return null;
		}

		if (body instanceof String || TypeUtils.isPrimitiveOrWrapper(body.getClass())) {
			return body.toString();
		} else if (body instanceof ToMap) {
			return jsonSupport.toJSONString(XUtils.toMap((ToMap) body));
		} else {
			return jsonSupport.toJSONString(body);
		}
	}

	@SuppressWarnings("rawtypes")
	public static String toFormString(Object body, String charsetName, JSONSupport jsonSupport)
			throws UnsupportedEncodingException {
		if (body == null) {
			return null;
		}

		if (body instanceof String || TypeUtils.isPrimitiveOrWrapper(body.getClass())) {
			return body.toString();
		} else if (body instanceof ToMap) {
			return toFormBody(((ToMap) body).toMap(), charsetName);
		} else if (body instanceof Map) {
			return toFormBody((Map) body, charsetName);
		} else {
			String json = jsonSupport.toJSONString(body);
			Map map = jsonSupport.parseObject(json, Map.class);
			return toFormBody(map, charsetName);
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

	public static MultiValueMap<String, String> toFormMap(String form) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		String[] kvArray = StringUtils.split(form, '&');
		for (String kv : kvArray) {
			int index = kv.indexOf("=");
			if (index == -1) {
				continue;
			}

			map.add(kv.substring(0, index), kv.substring(index, kv.length()));
		}
		return map;
	}

	@SuppressWarnings("rawtypes")
	public static String toFormBody(Map<?, ?> parameterMap, String charsetName) throws UnsupportedEncodingException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (Entry<?, ?> entry : parameterMap.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			String key = entry.getKey().toString();
			String text;
			if (value instanceof Collection) {
				text = toFormBody(key, (Collection) value, charsetName);
			} else if (value.getClass().isArray()) {
				text = toFormBody(key, ArrayUtils.toList(value), charsetName);
			} else {
				text = toFormBody(key, Arrays.asList(value), charsetName);
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
			throw new NotSupportedException(e);
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
			throw new NotSupportedException(e);
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
	
	public static boolean isValidOrigin(HttpRequest request, Collection<String> allowedOrigins) {
		Assert.notNull(request, "Request must not be null");
		Assert.notNull(allowedOrigins, "Allowed origins must not be null");

		String origin = request.getHeaders().getOrigin();
		if (origin == null || allowedOrigins.contains("*")) {
			return true;
		}
		else if (CollectionUtils.isEmpty(allowedOrigins)) {
			return isSameOrigin(request);
		}
		else {
			return allowedOrigins.contains(origin);
		}
	}

	/**
	 * 是否是同一个origin
	 * @param request
	 * @return
	 */
	public static boolean isSameOrigin(HttpRequest request) {
		HttpHeaders headers = request.getHeaders();
		String origin = headers.getOrigin();
		if (origin == null) {
			return true;
		}

		URI uri = request.getURI();
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort();

		UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
		return (ObjectUtils.nullSafeEquals(scheme, originUrl.getScheme())
				&& ObjectUtils.nullSafeEquals(host, originUrl.getHost())
				&& getPort(scheme, port) == getPort(originUrl.getScheme(), originUrl.getPort()));
	}

	private static int getPort(String scheme, int port) {
		if (port == -1) {
			if ("http".equals(scheme) || "ws".equals(scheme)) {
				port = 80;
			} else if ("https".equals(scheme) || "wss".equals(scheme)) {
				port = 443;
			}
		}
		return port;
	}
}
