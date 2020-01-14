package scw.net.http.client;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.ByteArray;
import scw.net.AbstractResponseCallback;
import scw.net.ByteArrayResponseCallback;
import scw.net.MimeTypeUtils;
import scw.net.NetworkUtils;
import scw.net.ResponseCallback;
import scw.net.http.FormRequest;
import scw.net.http.HttpRequest;
import scw.net.http.Method;

public abstract class AbstractHttpClient implements HttpClient {
	private static final String SET_COOKIE = "Set-Cookie";
	private static final String COOKIE = "cookie";

	public <T> T invoke(final HttpRequest request, final AbstractResponseCallback<T> response) {
		requestFilter(request);
		return NetworkUtils.execute(request, new ResponseCallback<T>() {

			public T response(URLConnection urlConnection) throws Throwable {
				HttpURLConnection res = (HttpURLConnection) urlConnection;
				responseFilter(request.getURL(), res);
				return response.response(res);
			}

		});
	}

	protected abstract String getCharsetName();

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
			List<String> cookies = CollectionUtils.getValue(response.getHeaderFields(), true, null, SET_COOKIE);
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
		httpRequest.setContentType(MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED.toString());
		ByteArray byteArray = invoke(httpRequest, new ByteArrayResponseCallback());
		return byteArray.toString(getCharsetName());
	}

	public String doPost(String url, Map<String, ?> parameterMap) {
		FormRequest request = new FormRequest(Method.POST, url, getCharsetName());
		request.addAll(parameterMap);
		ByteArray byteArray = invoke(request, new ByteArrayResponseCallback());
		return byteArray.toString(getCharsetName());
	}
}
