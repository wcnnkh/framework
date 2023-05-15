package io.basc.framework.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.message.Headers;
import io.basc.framework.net.message.Message;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.message.multipart.MultipartMessageResolver;
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
	private static final MultipartMessageResolver MULTIPART_MESSAGE_RESOLVER = Sys.getEnv()
			.getServiceLoader(MultipartMessageResolver.class).getServices().first();

	@Nullable
	public static MultipartMessageResolver getMultipartMessageResolver() {
		return MULTIPART_MESSAGE_RESOLVER;
	}

	public static BalancedInetSocketAddress parseInetSocketAddress(String addressTemplate, int defaultPort) {
		Assert.requiredArgument(StringUtils.hasText(addressTemplate), "addressTemplate");
		String[] vs = StringUtils.splitToArray(addressTemplate, ":");
		String h = vs[0];
		int port = defaultPort;
		if (vs.length == 2) {
			port = Integer.parseInt(vs[1]);
		}

		if (vs.length == 3) {
			return new BalancedInetSocketAddress(h, port, Integer.parseInt(vs[2]));
		} else {
			return new BalancedInetSocketAddress(h, port);
		}
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
	 * @param ip The input.
	 * @return {@code true}: yes<br>
	 *         {@code false}: no
	 */
	public static boolean isIP(final CharSequence ip) {
		return StringUtils.isNotEmpty(ip) && Pattern.matches(REGEX_IP, ip);
	}

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

	public static final Predicate<NetworkInterface> LOCAL_IP_NETWORK_INTERFACE_ACCEPT = new Predicate<NetworkInterface>() {
		public boolean test(NetworkInterface networkInterface) {
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

	public static final Predicate<InetAddress> IPV4_INET_ADDRESS_ACCEPT = new Predicate<InetAddress>() {
		public boolean test(InetAddress inetAddress) {
			return inetAddress instanceof Inet4Address;
		};
	};

	public static Set<InetAddress> getLocalIpAddresses(boolean ipv4) {
		return getLocalIpAddresses(ipv4 ? IPV4_INET_ADDRESS_ACCEPT : null);
	}

	public static Set<InetAddress> getLocalIpAddresses(Predicate<InetAddress> accept) {
		return getLocalIpAddresses(LOCAL_IP_NETWORK_INTERFACE_ACCEPT, accept);
	}

	public static Set<InetAddress> getLocalIpAddresses(Predicate<NetworkInterface> networkInterfaceAccept,
			Predicate<InetAddress> accept) {
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
					|| (networkInterfaceAccept != null && !networkInterfaceAccept.test(netInterface))) {
				continue;
			}

			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (address == null || (accept != null && !accept.test(address))) {
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

	public static boolean isAvailablePort(int port) {
		if (port < 0 || port > 65535) {
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

	public static int getAvailablePort(int minPort, int maxPort) {
		for (int i = Math.max(0, minPort), max = Math.min(65535, maxPort); i <= max; i++) {
			if (isAvailablePort(i)) {
				return i;
			}
		}
		throw new IllegalStateException("No ports available(" + minPort + "~" + maxPort + ")");
	}

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
