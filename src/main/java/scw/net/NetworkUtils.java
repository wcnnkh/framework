package scw.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.security.ssl.TrustAllManager;

public final class NetworkUtils {
	private NetworkUtils() {
	};

	private static final Response<Message> MESSAGE_RESPONSE = new DefaultAutoMessageResponse();
	/**
	 * 一个信任所有的ssl socket factory <br/>
	 * 注意:在初始化失败后可能为空
	 */
	public static final SSLSocketFactory TRUSE_ALL_SSL_SOCKET_FACTORY;

	static {
		// 创建一个信任所有的
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new TrustAllManager();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = null;
		try {
			sc = javax.net.ssl.SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		TRUSE_ALL_SSL_SOCKET_FACTORY = sc == null ? null : sc.getSocketFactory();
	}

	public static <T> T execute(URLConnection urlConnection, Request request, Response<T> response) throws Throwable {
		request.request(urlConnection);
		return response.response(urlConnection);
	}

	public static Message execute(URLConnection urlConnection, Request request) throws Throwable {
		return execute(urlConnection, request, MESSAGE_RESPONSE);
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

	public static Message execute(URL url, Proxy proxy, Request request) {
		return execute(url, proxy, request, MESSAGE_RESPONSE);
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

	public static Message execute(String url, Proxy proxy, Request request) {
		return execute(url, proxy, request, MESSAGE_RESPONSE);
	}

	public static <T> T execute(URLRequest request, Response<T> response) {
		return execute(request.getURL(), request.getProxy(), request, response);
	}

	public static Message execute(URLRequest request) {
		return execute(request, MESSAGE_RESPONSE);
	}

	public static List<InetSocketAddress> parseInetSocketAddressList(String address) {
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		String[] arr = StringUtils.commonSplit(address);
		for (String a : arr) {
			String[] vs = a.split(":");
			String h = vs[0];
			int port = 11211;
			if (vs.length == 2) {
				port = Integer.parseInt(vs[1]);
			}

			addresses.add(new InetSocketAddress(h, port));
		}
		return addresses;
	}

	public static boolean checkPortCccupied(InetAddress inetAddress, int port) {
		Socket socket = null;
		try {
			socket = new Socket(inetAddress, port);
			return true;
		} catch (IOException e) {
			// ignore
		} finally {
			IOUtils.close(false, socket);
		}
		return false;
	}

	/**
	 * 检查端口号占用
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	public static boolean checkPortCccupied(String host, int port) {
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			return true;
		} catch (IOException e) {
			// ignore
		} finally {
			IOUtils.close(false, socket);
		}
		return false;
	}

	/**
	 * 检查本地端口是否被占用
	 * 
	 * @param port
	 * @return
	 */
	public static boolean checkLocalPortCccupied(int port) {
		return checkPortCccupied("127.0.0.1", port);
	}
}
