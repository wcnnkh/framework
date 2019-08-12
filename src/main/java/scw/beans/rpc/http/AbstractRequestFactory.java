package scw.beans.rpc.http;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.annotation.Http;
import scw.core.StringFormat;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.net.http.HttpRequest;
import scw.net.http.HttpUtils;
import scw.servlet.ServletUtils;

public abstract class AbstractRequestFactory implements RequestFactory {
	private String charsetName;

	public AbstractRequestFactory(String charsetName) {
		this.charsetName = charsetName;
	}

	protected Map<String, String> getRequestHeaderMap() {
		return ServletUtils.getSpreadHeaderMap();
	}

	protected Map<String, Object> getParameterMap(Method method, Object[] args) {
		String[] names = ClassUtils.getParameterName(method);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (!ArrayUtils.isEmpty(names)) {
			for (int i = 0; i < names.length; i++) {
				map.put(names[i], args[i]);
			}
		}
		return map;
	}

	protected void processRequestParameterMap(Map<String, Object> parameterMap) {
		ServletUtils.appendSpreadData(parameterMap);
	}

	protected abstract void writeParameters(Map<String, Object> parameterMap,
			OutputStream output) throws Exception;

	protected abstract void afterHttpRequest(HttpRequest httpRequest)
			throws Exception;

	public HttpRequest createHttpRequest(Class<?> clazz, Method method,
			String host, Object[] args) throws Exception {
		Http http = method.getAnnotation(Http.class);
		String path = http == null ? ("/" + method.getName()) : http.value();
		final scw.net.http.Method requestMethod = http == null ? scw.net.http.Method.GET
				: http.method();

		final Map<String, Object> parameterMap = getParameterMap(method, args);
		processRequestParameterMap(parameterMap);
		StringFormat stringFormat = new StringFormat("{", "}") {
			public String getProperty(String key) {
				Object value = parameterMap.remove(key);
				return value == null ? "null" : value.toString();
			};
		};

		String url = stringFormat.format(path);
		if (!url.startsWith("/") && !host.endsWith("/")) {
			url = "/" + url;
		}
		url = host + url;
		url = requestMethod == scw.net.http.Method.GET ? HttpUtils
				.appendParameters(url, parameterMap, charsetName) : url;

		HttpRequest httpRequest = new HttpRequest(requestMethod, url) {
			@Override
			protected void doOutput(URLConnection urlConnection, OutputStream os)
					throws Throwable {
				Map<String, String> header = getRequestHeaderMap();
				if (!CollectionUtils.isEmpty(header)) {
					for (Entry<String, String> entry : header.entrySet()) {
						if (StringUtils.isEmpty(entry.getValue())) {
							continue;
						}

						urlConnection.setRequestProperty(entry.getKey(),
								entry.getValue());
					}
				}

				if (requestMethod != scw.net.http.Method.GET) {
					writeParameters(parameterMap, os);
				}

				super.doOutput(urlConnection, os);
			}
		};
		afterHttpRequest(httpRequest);
		return httpRequest;
	}

	public final String getCharsetName() {
		return charsetName;
	}

	public final void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
}
