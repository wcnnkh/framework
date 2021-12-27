package io.basc.framework.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.message.Headers;
import io.basc.framework.net.message.Message;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.message.multipart.MultipartMessageResolver;
import io.basc.framework.util.Accept;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public final class InetUtils {
	private InetUtils() {
	};

	private static final String MESSAGE_ID = "io-basc-framework-message-id";

	/**
	 * 本地ip
	 */
	private static final String[] LOCAL_IP = new String[] { "127.0.0.1", "0:0:0:0:0:0:0:1", "::1" };

	/**
	 * Regex of ip address.
	 */
	private static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
	private static final String INNER_IP_PATTERN = "((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))"
			+ "(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|"
			+ "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})";
	private static final MultipartMessageResolver MULTIPART_MESSAGE_RESOLVER = Sys.env
			.getServiceLoader(MultipartMessageResolver.class).first();

	@Nullable
	public static MultipartMessageResolver getMultipartMessageResolver() {
		return MULTIPART_MESSAGE_RESOLVER;
	}

	public static List<InetSocketAddress> parseInetSocketAddressList(String address) {
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		String[] arr = StringUtils.splitToArray(address);
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

	public static void writeHeader(Message inputMessage, OutputMessage outputMessage) throws IOException {
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

	public static boolean isLocalIP(String ip) {
		if (StringUtils.isEmpty(ip)) {
			return false;
		}

		for (String local : LOCAL_IP) {
			if (local.equals(ip)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return whether input matches regex of ip address.
	 *
	 * @param input The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isIP(final CharSequence ip) {
		return StringUtils.isNotEmpty(ip) && Pattern.matches(REGEX_IP, ip);
	}

	/**
	 * 判断是否是内网IP
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isInnerIP(String ip) {
		Pattern p = Pattern.compile(INNER_IP_PATTERN);
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}

	public static String getFilename(String url) {
		String urlToUse = url;
		int prefixIndex = urlToUse.indexOf("//");
		if (prefixIndex != -1) {
			urlToUse = url.substring(prefixIndex + 2);
		}

		int index = urlToUse.indexOf("/");
		if (index == -1 && prefixIndex != -1) {
			return null;
		}

		if (index != -1) {
			urlToUse = urlToUse.substring(index);
		}

		index = urlToUse.indexOf("#");
		if (index != -1) {
			urlToUse = url.substring(0, index);
		}

		index = urlToUse.indexOf("?");
		if (index != -1) {
			urlToUse = url.substring(0, index);
		}

		return StringUtils.getFilename(urlToUse);
	}

	/**
	 * 排除虚拟接口和没有启动运行的接口
	 */
	public static final Accept<NetworkInterface> LOCAL_IP_NETWORK_INTERFACE_ACCEPT = new Accept<NetworkInterface>() {
		public boolean accept(NetworkInterface networkInterface) {
			if (networkInterface.isVirtual()) {
				return false;
			}

			try {
				if (!networkInterface.isUp()) {
					return false;
				}
			} catch (SocketException e) {
				return false;
			}
			return true;
		};
	};

	public static final Accept<InetAddress> IPV4_INET_ADDRESS_ACCEPT = new Accept<InetAddress>() {
		public boolean accept(InetAddress inetAddress) {
			return inetAddress instanceof Inet4Address;
		};
	};

	public static Set<InetAddress> getLocalIpAddresses(boolean ipv4) {
		return getLocalIpAddresses(ipv4 ? IPV4_INET_ADDRESS_ACCEPT : null);
	}

	public static Set<InetAddress> getLocalIpAddresses(Accept<InetAddress> accept) {
		return getLocalIpAddresses(LOCAL_IP_NETWORK_INTERFACE_ACCEPT, accept);
	}

	public static Set<InetAddress> getLocalIpAddresses(Accept<NetworkInterface> networkInterfaceAccept,
			Accept<InetAddress> accept) {
		Enumeration<NetworkInterface> allNetInterfaces;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return Collections.emptySet();
		}

		Set<InetAddress> ips = new LinkedHashSet<InetAddress>(8);
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = allNetInterfaces.nextElement();
			if (netInterface == null
					|| (networkInterfaceAccept != null && !networkInterfaceAccept.accept(netInterface))) {
				continue;
			}

			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (address == null || (accept != null && !accept.accept(address))) {
					continue;
				}

				if (isLocalIP(address.getHostAddress())) {
					continue;
				}

				ips.add(address);
			}
		}
		return ips;
	}

	public static boolean isUrl(String url) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}

		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public static String getMessageId(Message input, @Nullable Message output) {
		Assert.requiredArgument(input != null, "input");
		String messageId = input.getHeaders().getFirst(MESSAGE_ID);
		if (messageId != null) {
			return messageId;
		}

		if (output != null && input != output) {
			messageId = output.getHeaders().getFirst(MESSAGE_ID);
			if (messageId != null) {
				return messageId;
			}
		}

		messageId = XUtils.getUUID();
		if (output != null) {
			output.getHeaders().set(MESSAGE_ID, messageId);
		}
		return messageId;
	}

	/**
	 * 端口是否可用
	 * 
	 * @param port
	 * @return
	 */
	public static boolean isAvailablePort(int port) {
		if(port < 0 || port > 65535) {
			return false;
		}
		
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(port);
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 获取指定范围内可用的端口(minPort<=port<=maxPort) {@linkplain 0 and 65535}
	 * 
	 * @param minPort
	 * @param maxPort
	 * @return
	 */
	public static int getAvailablePort(int minPort, int maxPort) {
		for (int i = Math.max(0, minPort), max = Math.min(65535, maxPort); i <= max; i++) {
			if (isAvailablePort(i)) {
				return i;
			}
		}
		throw new IllegalStateException("No ports available(" + minPort + "~" + maxPort + ")");
	}

	/**
	 * 获取一个可用的端口号
	 * 
	 * @return
	 */
	public static int getAvailablePort() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(0);
			return socket.getLocalPort();
		} catch (SocketException e) {
			throw new IllegalStateException("No ports available");
		} finally {
			if (socket != null) {
				try {
					if (socket.isConnected()) {
						socket.disconnect();
					}
				} finally {
					socket.close();
				}
			}
		}
	}
}
