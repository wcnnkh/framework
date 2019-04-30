package scw.core.net.http.client;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;

import scw.core.io.ByteArray;
import scw.core.net.AbstractResponse;
import scw.core.net.ByteArrayResponse;
import scw.core.net.NetworkUtils;
import scw.core.net.Response;
import scw.core.net.http.FormRequest;
import scw.core.net.http.HttpRequest;
import scw.core.net.http.enums.Method;

/**
 * TODO 还未完成
 * @author shuchaowen
 *
 */
public class DefaultHttpClient implements HttpClient {
	private final Charset charset;

	public DefaultHttpClient(Charset charset) {
		this.charset = charset;
	}

	public <T> T invoke(final HttpRequest request, final AbstractResponse<T> response) {
		requestFilter(request);
		return NetworkUtils.execute(request, new Response<T>() {

			public T response(URLConnection urlConnection) throws Throwable {
				HttpURLConnection res = (HttpURLConnection) urlConnection;
				responseFilter(request.getRequestAddress(), res);
				return response.response(res);
			}

		});
	}

	public Charset getCharset() {
		return charset;
	}

	protected void requestFilter(HttpRequest request) {
	}

	protected void responseFilter(String url, HttpURLConnection response) {
	}

	public String doGet(String url) {
		HttpRequest httpRequest = new HttpRequest(Method.GET, url);
		httpRequest.setContentTypeByAJAX(charset.name());
		ByteArray byteArray = invoke(httpRequest, new ByteArrayResponse());
		return byteArray.toString(charset);
	}

	public String doPost(String url, Map<String, Object> parameterMap) {
		FormRequest request = new FormRequest(Method.POST, url, charset.name());
		request.addAll(parameterMap);
		ByteArray byteArray = invoke(request, new ByteArrayResponse());
		return byteArray.toString(charset);
	}
}
