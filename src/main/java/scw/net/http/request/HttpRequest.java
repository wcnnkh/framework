package scw.net.http.request;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.net.AbstractUrlRequest;
import scw.net.RequestException;
import scw.net.http.enums.Method;

public class HttpRequest extends AbstractUrlRequest {
	protected final Method method;
	private Map<String, String> requestProperties;
	private String url;

	public HttpRequest(Method method, String url) {
		this.method = method;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void request(URLConnection urlConnection) throws Throwable {
		HttpURLConnection http = (HttpURLConnection) urlConnection;
		http.setRequestMethod(method.name());

		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);
		if (method != Method.GET) {
			urlConnection.setDoOutput(true);
		}
		urlConnection.setDoInput(true);

		if (requestProperties != null) {
			for (Entry<String, String> entry : requestProperties.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		super.request(urlConnection);
	}

	public void doOutput(OutputStream os) throws Throwable {
	}

	public void setRequestProperties(String key, Object value) {
		if (value == null) {
			return;
		}

		if (requestProperties == null) {
			requestProperties = new HashMap<String, String>();
		}
		requestProperties.put(key, value.toString());
	}

	public Method getMethod() {
		return method;
	}

	public void setRequestContentType(String contentType) {
		setRequestProperties("Content-Type", contentType);
	}

	public void setRequestProperties(Map<String, String> requestProperties) {
		this.requestProperties = requestProperties;
	}

	@Override
	public URL getURL() {
		try {
			return new URL(getUrl());
		} catch (MalformedURLException e) {
			throw new RequestException(url, e);
		}
	}

	@Override
	public Proxy getProxy() {
		return null;
	}

}
