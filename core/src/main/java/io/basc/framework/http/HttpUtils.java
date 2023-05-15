package io.basc.framework.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collection;

import io.basc.framework.env.Sys;
import io.basc.framework.factory.InheritableThreadLocalConfigurator;
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
	private static final InheritableThreadLocalConfigurator<HttpClient> CONFIGURATOR = new InheritableThreadLocalConfigurator<>(
			HttpClient.class)
			.ifAbsentDefaultService(() -> Sys.getEnv().getServiceLoader(HttpClient.class, DefaultHttpClient.class)
					.getServices().first());

	public static HttpClient getClient() {
		return CONFIGURATOR.get();
	};

	public static InheritableThreadLocalConfigurator<HttpClient> getConfigurator() {
		return CONFIGURATOR;
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

	public static boolean isSameOrigin(HttpRequest request) {
		if (request == null) {
			return false;
		}

		HttpHeaders headers = request.getHeaders();
		String origin = headers.getOrigin();
		if (origin == null) {
			return true;
		}

		return isSameOrigin(request.getURI(), UriComponentsBuilder.fromOriginHeader(origin).build().toUri());
	}

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

		if (charsetToUse == null) {
			charsetToUse = Constants.UTF_8;
		}

		ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
				.filename(fileName, charsetToUse).build();
		outputMessage.getHeaders().setContentDisposition(contentDisposition);
	}

	private HttpUtils() {
	}
}
