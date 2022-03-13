package io.basc.framework.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collection;

import io.basc.framework.env.Sys;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;

public final class HttpUtils {
	private HttpUtils() {
	};

	private static final HttpClient HTTP_CLIENT = Sys.env.getServiceLoader(HttpClient.class, DefaultHttpClient.class)
			.first();

	/**
	 * 获取默认的HttpClient(获取spi机制加载)
	 * 
	 * @return
	 */
	public static HttpClient getHttpClient() {
		return HTTP_CLIENT;
	}

	public static boolean isValidOrigin(HttpRequest request, Collection<String> allowedOrigins) {
		Assert.notNull(request, "Request must not be null");
		Assert.notNull(allowedOrigins, "Allowed origins must not be null");

		String origin = request.getHeaders().getOrigin();
		if (origin == null || allowedOrigins.contains("*")) {
			return true;
		} else if (CollectionUtils.isEmpty(allowedOrigins)) {
			return isSameOrigin(request);
		} else {
			return allowedOrigins.contains(origin);
		}
	}

	/**
	 * 是否是同一个origin
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isSameOrigin(HttpRequest request) {
		if(request == null) {
			return false;
		}
		
		HttpHeaders headers = request.getHeaders();
		String origin = headers.getOrigin();
		if (origin == null) {
			return true;
		}

		return isSameOrigin(request.getURI(), UriComponentsBuilder.fromOriginHeader(origin).build().toUri());
	}

	/**
	 * 判断两个url是否同源
	 * 
	 * @param url1
	 * @param url2
	 * @return
	 */
	public static boolean isSameOrigin(String url1, String url2) {
		if (url1 == null || url2 == null) {
			return false;
		}

		if (StringUtils.equals(url1, url2)) {
			return true;
		}

		try {
			return isSameOrigin(new URI(url1), new URI(url2));
		} catch (URISyntaxException e) {
			return false;
		}
	}

	/**
	 * 判断两个uri是否同源
	 * 
	 * @param uri1
	 * @param uri2
	 * @return
	 */
	public static boolean isSameOrigin(URI uri1, URI uri2) {
		if (uri1 == null || uri2 == null) {
			return false;
		}

		if (uri1.equals(uri2)) {
			return true;
		}

		return (ObjectUtils.equals(uri1.getScheme(), uri2.getScheme())
				&& ObjectUtils.equals(uri1.getHost(), uri2.getHost())
				&& getPort(uri1.getScheme(), uri1.getPort()) == getPort(uri2.getScheme(), uri2.getPort()));
	}

	private static int getPort(String scheme, int port) {
		if (port == -1) {
			if ("http".equals(scheme) || "ws".equals(scheme)) {
				port = 80;
			} else if ("https".equals(scheme) || "wss".equals(scheme)) {
				port = 443;
			}
		}
		return port;
	}

	/**
	 * 将文件信息写入ContentDisposition
	 * 
	 * @param outputMessage
	 * @param fileName
	 * @param charset
	 */
	public static void writeFileMessageHeaders(HttpOutputMessage outputMessage, String fileName,
			@Nullable Charset charset) {
		Assert.requiredArgument(outputMessage != null, "outputMessage");
		Assert.requiredArgument(fileName != null, "fileName");
		MimeType mimeType = FileMimeTypeUitls.getMimeType(fileName);
		if (mimeType != null) {
			outputMessage.setContentType(mimeType);
		}

		Charset charsetToUse = charset;
		if (charsetToUse == null) {
			charsetToUse = outputMessage.getCharset();
		}
		
		if(charsetToUse == null) {
			charsetToUse = Constants.UTF_8;
		}

		ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
				.filename(fileName, charsetToUse).build();
		outputMessage.getHeaders().setContentDisposition(contentDisposition);
	}
}
