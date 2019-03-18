package scw.net;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import scw.core.NestedRuntimeException;
import scw.net.response.Body;
import scw.net.response.BodyResponse;

public final class NetworkUtils {
	private NetworkUtils() {
	};

	public static <T> T execute(URLConnection urlConnection, Request request, Response<T> response) throws Throwable {
		request.request(urlConnection);
		return response.response(urlConnection);
	}

	public static <T> T executeHttp(URL url, Request request, Response<T> response) {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			return execute(httpURLConnection, request, response);
		} catch (Throwable e) {
			throw new NestedRuntimeException(e);
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static <T> T executeHttp(URL url, Proxy proxy, Request request, Response<T> response) {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
			return execute(httpURLConnection, request, response);
		} catch (Throwable e) {
			throw new NestedRuntimeException(e);
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static <T> T executeHttp(String url, Request request, Response<T> response) {
		URL u;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			throw new NestedRuntimeException(e);
		}
		return executeHttp(u, request, response);
	}

	public static Body executeHttp(String url, Request request) {
		return executeHttp(url, request, new BodyResponse());
	}
}
