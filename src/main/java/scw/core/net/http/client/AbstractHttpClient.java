package scw.core.net.http.client;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import scw.core.io.ByteArray;
import scw.core.net.AbstractResponse;
import scw.core.net.ByteArrayResponse;
import scw.core.net.NetworkUtils;
import scw.core.net.Response;
import scw.core.net.http.FormRequest;
import scw.core.net.http.HttpRequest;
import scw.core.net.http.enums.Method;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

public abstract class AbstractHttpClient implements HttpClient {
	private static final String SET_COOKIE = "Set-Cookie";
	private static final String COOKIE = "cookie";

	public <T> T invoke(final HttpRequest request, final AbstractResponse<T> response) {
		requestFilter(request);
		return NetworkUtils.execute(request, new Response<T>() {

			public T response(URLConnection urlConnection) throws Throwable {
				HttpURLConnection res = (HttpURLConnection) urlConnection;
				responseFilter(request.getURL(), res);
				return response.response(res);
			}

		});
	}

	protected abstract Charset getCharset();

	protected abstract CookieManager getCookieManager();

	protected void requestFilter(HttpRequest request) {
		CookieManager cookieManager = getCookieManager();
		if (cookieManager != null) {
			String cookies = cookieManager.getCookie(request.getURL());
			if (!StringUtils.isEmpty(cookies)) {
				request.setRequestProperties(COOKIE, cookies);
			}
		}
	}

	protected void responseFilter(URL url, HttpURLConnection response) {
		CookieManager cookieManager = getCookieManager();
		if (cookieManager != null) {
			List<String> cookies = CollectionUtils.getValueByMap(response.getHeaderFields(), SET_COOKIE, true, null);
			if (!CollectionUtils.isEmpty(cookies)) {
				for (String cookie : cookies) {
					if (!StringUtils.isEmpty(cookie)) {
						cookieManager.setCookie(url, cookie);
					}
				}
			}
		}
	}

	public String doGet(String url) {
		HttpRequest httpRequest = new HttpRequest(Method.GET, url);
		httpRequest.setContentTypeByAJAX(getCharset().name());
		ByteArray byteArray = invoke(httpRequest, new ByteArrayResponse());
		return byteArray.toString(getCharset());
	}

	public String doPost(String url, Map<String, ?> parameterMap) {
		FormRequest request = new FormRequest(Method.POST, url, getCharset().name());
		request.addAll(parameterMap);
		ByteArray byteArray = invoke(request, new ByteArrayResponse());
		return byteArray.toString(getCharset());
	}
}
