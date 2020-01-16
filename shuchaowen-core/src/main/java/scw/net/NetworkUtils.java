package scw.net;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.message.converter.ByteArrayMessageConveter;
import scw.net.message.converter.DefaultMessageConverterChain;
import scw.net.message.converter.JsonMessageConverter;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MessageConverterChain;
import scw.net.message.converter.StringMessageConverter;
import scw.net.ssl.TrustAllManager;

public final class NetworkUtils {
	private NetworkUtils() {
	};

	/**
	 * 一个信任所有的ssl socket factory <br/>
	 * 注意:在初始化失败后可能为空
	 */
	public static final SSLSocketFactory TRUSE_ALL_SSL_SOCKET_FACTORY;
	private static final LinkedList<MessageConverter> MESSAGE_CONVERTERS = new LinkedList<MessageConverter>();

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

		MESSAGE_CONVERTERS.add(new ByteArrayMessageConveter());
		MESSAGE_CONVERTERS.add(new StringMessageConverter());
		MESSAGE_CONVERTERS.add(new JsonMessageConverter());
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

	public static Collection<MessageConverter> getMessageConverters() {
		return Collections.unmodifiableCollection(MESSAGE_CONVERTERS);
	}

	public static Object read(Type type, InputMessage inputMessage,
			Collection<? extends MessageConverter> messageConverters) throws IOException {
		MessageConverterChain chain = new DefaultMessageConverterChain(messageConverters, null);
		return chain.read(type, inputMessage);
	}

	public static void write(Object body, MimeType contentType, OutputMessage outputMessage,
			Collection<? extends MessageConverter> messageConverters) throws IOException {
		MessageConverterChain chain = new DefaultMessageConverterChain(messageConverters, null);
		chain.write(body, contentType, outputMessage);
	}

	public static URI toURI(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Failed to URI", e);
		}
	}
}
