package scw.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.SSLSocketFactory;

import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.net.message.Headers;
import scw.net.message.Message;
import scw.net.message.OutputMessage;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.ssl.TrustAllManager;

public final class NetworkUtils {
	private NetworkUtils() {
	};

	/**
	 * 一个信任所有的ssl socket factory <br/>
	 * 注意:在初始化失败后可能为空
	 */
	public static final SSLSocketFactory TRUSE_ALL_SSL_SOCKET_FACTORY;

	private static final MultiMessageConverter MESSAGE_CONVERTER = new MultiMessageConverter();

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
		TRUSE_ALL_SSL_SOCKET_FACTORY = sc == null ? null : sc
				.getSocketFactory();
		
		MESSAGE_CONVERTER.addAll(InstanceUtils
				.getSystemConfigurationList(MessageConverter.class));
	}

	public static MultiMessageConverter getMessageConverter() {
		return MESSAGE_CONVERTER;
	}

	public static List<InetSocketAddress> parseInetSocketAddressList(
			String address) {
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

	public static URI toURI(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Failed to URI", e);
		}
	}

	public static void writeHeader(Message inputMessage,
			OutputMessage outputMessage) throws IOException {
		long len = outputMessage.getContentLength();
		if (len >= 0) {
			outputMessage.setContentLength(len);
		}

		MimeType mimeType = inputMessage.getContentType();
		if (mimeType != null) {
			outputMessage.setContentType(mimeType);
		}

		Headers headers = inputMessage.getHeaders();
		if (headers != null) {
			for (Entry<String, List<String>> entry : headers.entrySet()) {
				for (String value : entry.getValue()) {
					outputMessage.getHeaders().add(entry.getKey(), value);
				}
			}
		}
	}
}
