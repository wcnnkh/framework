package scw.servlet.http;

import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.StringFormat;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.ByteArray;
import scw.json.JSONUtils;
import scw.net.ContentType;
import scw.net.DefaultContentType;
import scw.net.NetworkUtils;
import scw.net.http.HttpRequest;
import scw.net.http.HttpUtils;
import scw.servlet.ServletUtils;

@SuppressWarnings("unchecked")
public final class HttpCallUitls {
	public static final String COOKIE_HEADER_NAME = "Cookie";
	private static final String HEAD_SOURCE_NAME = HttpCallUitls.class.getName() + "#header";

	private static Map<String, String> privateGetSpreadHeaderMap() {
		return (Map<String, String>) ServletUtils.getControllerThreadLocalResource(HEAD_SOURCE_NAME);
	}

	public static void setSpreadHeader(String name, String value) {
		Map<String, String> headerMap = privateGetSpreadHeaderMap();
		if (headerMap == null) {
			headerMap = new HashMap<String, String>(4);
			ServletUtils.bindControllerThreadLocalResource(HEAD_SOURCE_NAME, headerMap);
		}
		headerMap.put(name, value);
	}

	public static Map<String, String> getSpreadHeaderMap() {
		Map<String, String> headerMap = privateGetSpreadHeaderMap();
		return headerMap == null ? null : Collections.unmodifiableMap(headerMap);
	}

	public static String getSpreadHeader(String name) {
		Map<String, String> headerMap = privateGetSpreadHeaderMap();
		return headerMap == null ? null : headerMap.get(name);
	}

	public static void clearSpreadHeader() {
		ServletUtils.bindControllerThreadLocalResource(HEAD_SOURCE_NAME, null);
	}

	public static void removeSpreadHeader(String name) {
		Map<String, String> headerMap = privateGetSpreadHeaderMap();
		if (headerMap == null) {
			return;
		}
		headerMap.remove(name);
	}

	private static HashMap<String, Object> getSpreadData() {
		return (HashMap<String, Object>) ServletUtils.getControllerThreadLocalResource(HttpCallUitls.class);
	}

	public static void setSpreadData(String name) {
		HashMap<String, Object> map = getSpreadData();
		if (map == null) {
			return;
		}
		map.remove(name);
	}

	public static void setSpreadData(String name, Object value) {
		HashMap<String, Object> map = getSpreadData();
		if (map == null) {
			map = new HashMap<String, Object>(8);
			ServletUtils.bindControllerThreadLocalResource(HttpCallUitls.class, map);
		}
		map.put(name, value);
	}

	public static void removeSpreadData(String name) {
		Map<String, Object> map = getSpreadData();
		if (map == null) {
			return;
		}
		map.remove(name);
	}

	public static HttpRequest createHttpCallRequest(scw.net.http.Method method, String host,
			Map<String, Object> parameterMap, final String charsetName, final boolean form) throws Exception {
		final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		if (!CollectionUtils.isEmpty(parameterMap)) {
			map.putAll(parameterMap);
		}

		Map<String, Object> spreadMap = getSpreadData();
		if (!CollectionUtils.isEmpty(spreadMap)) {
			map.putAll(spreadMap);
		}

		StringFormat stringFormat = new StringFormat("{", "}") {
			public String getValue(String key) {
				Object value = map.remove(key);
				return value == null ? "null" : value.toString();
			};
		};

		String url = stringFormat.format(host);
		url = method == scw.net.http.Method.GET ? HttpUtils.appendParameters(url, map, charsetName) : url;

		HttpRequest httpRequest = new HttpRequest(method, url) {
			@Override
			protected void doOutput(URLConnection urlConnection, OutputStream os) throws Throwable {
				Map<String, String> header = privateGetSpreadHeaderMap();
				if (!CollectionUtils.isEmpty(header)) {
					for (Entry<String, String> entry : header.entrySet()) {
						if (StringUtils.isEmpty(entry.getValue())) {
							continue;
						}

						urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
					}
				}

				if (!CollectionUtils.isEmpty(map)) {
					if (form) {
						os.write(HttpUtils.appendParameters(null, map, charsetName).getBytes(charsetName));
					} else {
						os.write(JSONUtils.toJSONString(map).getBytes(charsetName));
					}
				}
				super.doOutput(urlConnection, os);
			}
		};

		if (form) {
			httpRequest
					.setContentType(new DefaultContentType(ContentType.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
		} else {
			httpRequest.setContentType(new DefaultContentType(ContentType.APPLICATION_JSON, charsetName));
		}
		return httpRequest;
	}

	public static ByteArray call(scw.net.http.Method method, String host, Map<String, Object> parameterMap,
			final String charsetName, final boolean form) throws Exception {
		return NetworkUtils.execute(createHttpCallRequest(method, host, parameterMap, charsetName, form));
	}
}
