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
import scw.core.string.StringCodecUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.io.IOUtils;
import scw.json.JSONUtils;
import scw.lang.NotSupportException;
import scw.net.RequestException;
import scw.net.http.client.ClientHttpRequest;
import scw.net.http.client.ClientHttpRequestFactory;
import scw.net.http.client.ClientHttpResponse;
import scw.net.http.client.SimpleClientHttpRequestFactory;
import scw.net.mime.MimeTypeUtils;
import scw.util.ToMap;

public final class HttpUtils {
	private HttpUtils() {
	};

	private static final ClientHttpRequestFactory CLIENT_HTTP_REQUEST_FACTORY = new SimpleClientHttpRequestFactory();

	public static ClientHttpRequestFactory getClientHttpRequestFactory() {
		return CLIENT_HTTP_REQUEST_FACTORY;
	}

	public static String doGet(String url) {
		return doGet(url, Constants.DEFAULT_CHARSET_NAME);
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

	public static String doGet(String url, String charsetName) {
		ClientHttpRequest request;
		ClientHttpResponse response = null;
		try {
			request = createRequest(url, Method.GET,
					new MediaType(MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED, charsetName),
					getClientHttpRequestFactory());
			response = request.execute();
			return response.convertToString(charsetName);
		} catch (IOException e) {
			throw new RequestException(e);
		} finally {
			IOUtils.close(response);
		}
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
		ClientHttpRequest request;
		ClientHttpResponse response = null;
		try {
			request = createRequest(url, Method.POST, new MediaType(MimeTypeUtils.APPLICATION_JSON, charsetName),
					getClientHttpRequestFactory());
			if (StringUtils.isNotEmpty(text)) {
				IOUtils.write(StringCodecUtils.getStringCodec(charsetName).encode(text), request.getBody());
			}
			response = request.execute();
			return response.convertToString(charsetName);
		} catch (IOException e) {
			throw new RequestException(e);
		} finally {
			IOUtils.close(response);
		}
	}

	public static String postJson(String url, Map<String, String> requestProperties, Object body) {
		return postJson(url, requestProperties, body, Constants.DEFAULT_CHARSET_NAME);
	}

	public static String postForm(String url, Map<String, String> requestProperties, Map<String, ?> parameterMap,
			String charsetName) {
		ClientHttpRequest request;
		ClientHttpResponse response = null;
		try {
			String body = toFormBody(parameterMap, charsetName);
			request = createRequest(url, Method.POST,
					new MediaType(MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED, charsetName),
					getClientHttpRequestFactory());
			request.getHeaders().setAll(requestProperties);
			if (StringUtils.isNotEmpty(body)) {
				IOUtils.write(StringCodecUtils.getStringCodec(charsetName).encode(body), request.getBody());
			}
			response = request.execute();
			return response.convertToString(charsetName);
		} catch (IOException e) {
			throw new RequestException(e);
		} finally {
			IOUtils.close(response);
		}
	}

	public static String postForm(String url, Map<String, String> requestProperties,
			ToMap<String, ?> toRequestParameterMap, String charsetName) {
		return postForm(url, requestProperties, XUtils.toMap(toRequestParameterMap), charsetName);
	}

	public static String postForm(String url, Map<String, String> requestProperties, Map<String, ?> parameterMap) {
		return postForm(url, requestProperties, parameterMap, Constants.DEFAULT_CHARSET_NAME);
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
