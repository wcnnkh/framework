package scw.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocketFactory;

import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.net.message.Headers;
import scw.net.message.Message;
import scw.net.message.OutputMessage;
import scw.net.message.converter.ByteArrayMessageConverter;
import scw.net.message.converter.HttpFormMessageConveter;
import scw.net.message.converter.JsonMessageConverter;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.message.converter.StringMessageConverter;
import scw.net.message.converter.XmlMessageConverter;
import scw.net.ssl.TrustAllManager;

public final class InetUtils {
	private InetUtils() {
	};

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
		TRUSE_ALL_SSL_SOCKET_FACTORY = sc == null ? null : sc.getSocketFactory();

		MESSAGE_CONVERTER.add(new JsonMessageConverter());
		MESSAGE_CONVERTER.add(new StringMessageConverter());
		MESSAGE_CONVERTER.add(new ByteArrayMessageConverter());
		MESSAGE_CONVERTER.add(new XmlMessageConverter());
		MESSAGE_CONVERTER.add(new HttpFormMessageConveter());
		MESSAGE_CONVERTER.addAll(InstanceUtils.loadAllService(MessageConverter.class));
	}

	public static MessageConverter getMessageConverter() {
		return MESSAGE_CONVERTER;
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

	public static URI toURI(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Failed to URI", e);
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

	/**
	 * 判断是否是json消息
	 * 
	 * @param serverRequest
	 * @return
	 */
	public static boolean isJsonMessage(Message message) {
		return isDesignatedContentTypeMessage(message, MimeTypeUtils.APPLICATION_JSON_VALUE)
				|| isDesignatedContentTypeMessage(message, MimeTypeUtils.TEXT_JSON_VALUE);
	}

	public static boolean isXmlMessage(Message message) {
		return isDesignatedContentTypeMessage(message, MimeTypeUtils.APPLICATION_XML_VALUE)
				|| isDesignatedContentTypeMessage(message, MimeTypeUtils.TEXT_XML_VALUE);
	}

	public static boolean isFormMessage(Message message) {
		return isDesignatedContentTypeMessage(message, MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED_VALUE);
	}

	public static boolean isMultipartMessage(Message message) {
		return isDesignatedContentTypeMessage(message, MimeTypeUtils.MULTIPART_FORM_DATA_VALUE);
	}

	public static boolean isDesignatedContentTypeMessage(Message message, String contentType) {
		MimeType mimeType = message.getContentType();
		if (mimeType == null) {
			return false;
		}
		return StringUtils.contains(mimeType.toString(), contentType, true);
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
	 * @param input
	 *            The input.
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

	public static void writeFileMessageHeaders(OutputMessage outputMessage, String fileName) throws IOException {
		String fileNameToUse = StringUtils.containsChinese(fileName) ? new String(fileName.getBytes(), "iso-8859-1")
				: fileName;
		MimeType mimeType = FileMimeTypeUitls.getMimeType(fileName);
		if (mimeType == null) {
			outputMessage.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
		} else {
			outputMessage.setContentType(mimeType);
		}
		outputMessage.getHeaders().add("Content-Disposition", "attachment;filename=" + fileNameToUse);
	}
}
