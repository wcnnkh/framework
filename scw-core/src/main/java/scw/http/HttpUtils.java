package scw.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import scw.core.Assert;
import scw.core.Constants;
import scw.core.instance.InstanceUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.http.client.HttpClient;
import scw.http.multipart.FileItemParser;
import scw.http.server.JsonServerHttpRequest;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.http.server.ip.ServerHttpRequestIpGetter;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.JsonObject;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.net.uri.UriComponentsBuilder;
import scw.util.XUtils;
import scw.value.EmptyValue;
import scw.value.StringValue;
import scw.value.Value;

public final class HttpUtils {
	private HttpUtils() {
	};

	private static final HttpClient HTTP_CLIENT = InstanceUtils.loadService(HttpClient.class,
			"scw.http.client.SimpleHttpClient");
	private static final ServerHttpRequestIpGetter SERVER_HTTP_REQUEST_IP_GETTER = InstanceUtils
			.loadService(ServerHttpRequestIpGetter.class, "scw.http.server.ip.DefaultServerHttpRequestIpGetter");
	private static final FileItemParser FILE_ITEM_PARSER = InstanceUtils.loadService(FileItemParser.class,
			"scw.http.multipart.ApacheFileItemParser");

	public static HttpClient getHttpClient() {
		return HTTP_CLIENT;
	}

	public static ServerHttpRequestIpGetter getServerHttpRequestIpGetter() {
		return SERVER_HTTP_REQUEST_IP_GETTER;
	}

	public static FileItemParser getFileItemParser() {
		return FILE_ITEM_PARSER;
	}

	public static boolean isSupportMultiPart() {
		return FILE_ITEM_PARSER != null;
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

		return (ObjectUtils.nullSafeEquals(uri1.getScheme(), uri2.getScheme())
				&& ObjectUtils.nullSafeEquals(uri1.getHost(), uri2.getHost())
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
	 * 从cookie中获取数据
	 * 
	 * @param request
	 * 
	 * @param name
	 *            cookie中的名字
	 * @return
	 */
	public static HttpCookie getCookie(ServerHttpRequest request, String name) {
		if (name == null) {
			return null;
		}

		HttpCookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}

		for (HttpCookie cookie : cookies) {
			if (cookie == null) {
				continue;
			}

			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	/**
	 * 将文件信息写入ContentDisposition
	 * 
	 * @param outputMessage
	 * @param fileName
	 */
	public static void writeFileMessageHeaders(HttpOutputMessage outputMessage, String fileName) {
		MimeType mimeType = FileMimeTypeUitls.getMimeType(fileName);
		if (mimeType != null) {
			outputMessage.setContentType(mimeType);
		}
		ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
				.filename(fileName, Constants.UTF_8).build();
		outputMessage.getHeaders().setContentDisposition(contentDisposition);
	}

	/**
	 * 缓存是否过期,如果未过期那么返回304，如果已过期则setLastModified
	 * 
	 * @param request
	 * @param response
	 * @param lastModified
	 * @return
	 */
	public static boolean isExpired(ServerHttpRequest request, ServerHttpResponse response, long lastModified) {
		response.getHeaders().setLastModified(lastModified);
		long ifModifiedSince = request.getHeaders().getIfModifiedSince();
		if (ifModifiedSince < 0 || lastModified < 0) {
			// 缓存已过期,请求中没有此值
			return true;
		}

		// 不比较毫秒
		if (ifModifiedSince / 1000 != lastModified / 1000) {
			// 缓存已过期
			return true;
		}

		// 客户端缓存未过期
		response.setStatusCode(HttpStatus.NOT_MODIFIED);
		return false;
	}

	/**
	 * 写入一个静态资源
	 * 
	 * @param request
	 * @param response
	 * @param resource
	 * @param mimeType
	 * @throws IOException
	 */
	public static void writeStaticResource(ServerHttpRequest request, ServerHttpResponse response, Resource resource,
			MimeType mimeType) throws IOException {
		if (!resource.exists()) {
			response.sendError(HttpStatus.NOT_FOUND.value(), "The resource does not exist!");
			return;
		}

		if (mimeType != null) {
			response.setContentType(mimeType);
		}

		if (!isExpired(request, response, resource.lastModified())) {
			return;
		}
		IOUtils.copy(resource.getInputStream(), response.getBody());
	}

	/**
	 * 根据参数名获取
	 * 
	 * @param request
	 * @param name
	 * @return 如果不存在返回{@see EmptyValue}
	 */
	public static Value getParameter(ServerHttpRequest request, String name) {
		String value = request.getParameterMap().getFirst(name);
		if (value != null) {
			return new StringValue(value);
		}

		JsonServerHttpRequest jsonServerHttpRequest = XUtils.getTarget(request, JsonServerHttpRequest.class);
		if (jsonServerHttpRequest != null) {
			JsonObject jsonObject = jsonServerHttpRequest.getJsonObject();
			if (jsonObject != null) {
				JsonElement element = jsonObject.get(name);
				if (element != null) {
					return element;
				}
			}
		}
		return EmptyValue.INSTANCE;
	}

	/**
	 * 此方法不会返回空，如果不存在返回的数组长度为0
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Value[] getParameterValues(ServerHttpRequest request, String name) {
		List<String> valueList = request.getParameterMap().get(name);
		if (!CollectionUtils.isEmpty(valueList)) {
			Value[] values = new Value[valueList.size()];
			int index = 0;
			for (String value : valueList) {
				values[index++] = new StringValue(value);
			}
			return values;
		}

		JsonServerHttpRequest jsonServerHttpRequest = XUtils.getTarget(request, JsonServerHttpRequest.class);
		if (jsonServerHttpRequest != null) {
			JsonObject jsonObject = jsonServerHttpRequest.getJsonObject();
			if (jsonObject != null) {
				JsonElement jsonElement = jsonObject.get(name);
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray = jsonElement.getAsJsonArray();
					Value[] values = new Value[jsonArray.size()];
					int index = 0;
					for (JsonElement element : jsonElement.getAsJsonArray()) {
						values[index++] = element;
					}
					return values;
				}
			}
		}
		return Value.EMPTY_ARRAY;
	}
}
