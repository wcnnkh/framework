package scw.net.http.request;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.net.AbstractRequest;
import scw.net.http.enums.Method;

public class HttpRequest extends AbstractRequest {
	protected final Method method;
	private Map<String, String> requestProperties;

	public HttpRequest(Method method) {
		this.method = method;
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
	
}
