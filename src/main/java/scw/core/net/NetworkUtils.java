package scw.core.net;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import scw.core.io.ByteArray;
import scw.core.net.response.ByteArrayResponse;

public final class NetworkUtils {
	private NetworkUtils() {
	};

	public static <T> T execute(URLConnection urlConnection, Request request, Response<T> response) throws Throwable {
		request.request(urlConnection);
		return response.response(urlConnection);
	}

	public static <T> T execute(URL url, Proxy proxy, Request request, Response<T> response) {
		URLConnection urlConnection = null;
		try {
			if (proxy == null) {
				urlConnection = url.openConnection();
			} else {
				urlConnection = url.openConnection(proxy);
			}

			return execute(urlConnection, request, response);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (urlConnection != null) {
				if (urlConnection instanceof HttpURLConnection) {
					((HttpURLConnection) urlConnection).disconnect();
				}
			}
		}
	}

	public static <T> T execute(String url, Proxy proxy, Request request, Response<T> response) {
		URL u = null;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			new RuntimeException(e);
		}

		if (u == null) {
			throw new NullPointerException(url);
		}

		return execute(u, proxy, request, response);
	}

	public static <T> T execute(AbstractUrlRequest request, Response<T> response) {
		return execute(request.getURL(), request.getProxy(), request, response);
	}

	public static ByteArray execute(AbstractUrlRequest request) {
		return execute(request, new ByteArrayResponse());
	}
}
