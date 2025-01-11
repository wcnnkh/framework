package io.basc.framework.web;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.core.convert.Value;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.Message;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.multipart.MultipartMessage;
import io.basc.framework.net.multipart.MultipartMessageResolver;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.codec.Decoder;
import io.basc.framework.util.codec.support.CharsetCodec;
import io.basc.framework.util.codec.support.URLCodec;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.MultiValueMap;
import io.basc.framework.util.io.IOUtils;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.xml.XmlUtils;

public final class WebUtils {
	private static Logger logger = LogManager.getLogger(WebUtils.class);
	private static final ThreadLocal<ServerHttpRequest> SERVER_HTTP_REQUEST_lOCAL = new NamedThreadLocal<ServerHttpRequest>(
			ServerHttpRequest.class.getName());

	private static final String RESTFUL_PARAMETER_MAP = "io.basc.framework.web.restful.parameters";
	public static final String PATH_SEPARATOR = "/";

	public static ThreadLocal<ServerHttpRequest> getServerHttpRequestLocal() {
		return SERVER_HTTP_REQUEST_lOCAL;
	}

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

	public static void writeStaticResource(ServerHttpRequest request, ServerHttpResponse response, Resource resource,
			MimeType mimeType) throws IOException {
		if (resource == null || !resource.exists()) {
			response.sendError(HttpStatus.NOT_FOUND.value(), "The resource does not exist!");
			return;
		}

		if (mimeType != null) {
			response.setContentType(mimeType);
		}

		if (!isExpired(request, response, resource.lastModified())) {
			return;
		}
		IOUtils.copy(resource.getInputStream(), response.getOutputStream());
	}

	public static Value getParameter(ServerHttpRequest request, String name) {
		String value = request.getParameterMap().getFirst(name);
		if (value == null) {
			Map<String, String> parameterMap = getRestfulParameterMap(request);
			if (parameterMap != null) {
				value = parameterMap.get(name);
			}
		}

		if (value != null) {
			value = decodeGETParameter(request, value);
			return Value.of(value);
		}

		JsonServerHttpRequest jsonServerHttpRequest = XUtils.getDelegate(request, JsonServerHttpRequest.class);
		if (jsonServerHttpRequest != null) {
			JsonObject jsonObject = jsonServerHttpRequest.getJsonObject();
			if (jsonObject != null) {
				JsonElement element = jsonObject.get(name);
				if (element != null) {
					return element;
				}
			}
		}

		MultiPartServerHttpRequest multiPartServerHttpRequest = XUtils.getDelegate(request,
				MultiPartServerHttpRequest.class);
		if (multiPartServerHttpRequest != null) {
			MultipartMessage multipartMessage = multiPartServerHttpRequest.getMultipartMessageMap().getFirst(name);
			if (multipartMessage != null) {
				return Value.of(multipartMessage);
			}
		}
		return Value.EMPTY;
	}

	public static Value[] getParameterValues(ServerHttpRequest request, String name) {
		List<String> valueList = request.getParameterMap().get(name);
		if (!CollectionUtils.isEmpty(valueList)) {
			Value[] values = new Value[valueList.size()];
			int index = 0;
			for (String value : valueList) {
				values[index++] = Value.of(decodeGETParameter(request, value));
			}
			return values;
		}

		JsonServerHttpRequest jsonServerHttpRequest = XUtils.getDelegate(request, JsonServerHttpRequest.class);
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

		MultiPartServerHttpRequest multiPartServerHttpRequest = XUtils.getDelegate(request,
				MultiPartServerHttpRequest.class);
		if (multiPartServerHttpRequest != null) {
			List<MultipartMessage> items = multiPartServerHttpRequest.getMultipartMessageMap().get(name);
			Value[] values = new Value[items.size()];
			int index = 0;
			for (MultipartMessage element : items) {
				values[index++] = Value.of(element);
			}
			return values;
		}

		return Value.EMPTY_ARRAY;
	}

	public static ServerHttpRequest wrapperServerJsonRequest(ServerHttpRequest request) {
		if (request.getMethod() == HttpMethod.GET) {
			return request;
		}

		// 如果是一个json请求，那么包装一下
		if (request.getHeaders().isJsonContentType()) {
			JsonServerHttpRequest jsonServerHttpRequest = XUtils.getDelegate(request, JsonServerHttpRequest.class);
			if (jsonServerHttpRequest != null) {
				// 返回原始对象
				return request;
			}

			return new JsonServerHttpRequest(request);
		}
		return request;
	}

	public static ServerHttpRequest wrapperServerMultipartFormRequest(ServerHttpRequest request,
			MultipartMessageResolver multipartMessageResolver) {
		if (request.getMethod() == HttpMethod.GET) {
			return request;
		}

		// 如果是 一个MultiParty请求，那么包装一下
		if (request.getHeaders().isMultipartFormContentType()) {
			MultiPartServerHttpRequest multiPartServerHttpRequest = XUtils.getDelegate(request,
					MultiPartServerHttpRequest.class);
			if (multiPartServerHttpRequest != null) {
				// 返回原始对象
				return request;
			}

			if (multipartMessageResolver == null) {
				logger.warn("Multipart is not supported: {}", request);
			} else {
				return new DefaultMultiPartServerHttpRequest(request, multipartMessageResolver);
			}
		}
		return request;
	}

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

	public static void setLocalServerHttpRequest(ServerHttpRequest request) {
		if (request == null) {
			SERVER_HTTP_REQUEST_lOCAL.remove();
		} else {
			SERVER_HTTP_REQUEST_lOCAL.set(request);
		}
	}

	public static ServerHttpRequest getLocalServerHttpRequest() {
		return SERVER_HTTP_REQUEST_lOCAL.get();
	}

	public static void setRestfulParameterMap(ServerHttpRequest request, Map<String, String> restfulMap) {
		request.setAttribute(RESTFUL_PARAMETER_MAP, restfulMap);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getRestfulParameterMap(ServerHttpRequest request) {
		return (Map<String, String>) request.getAttribute(RESTFUL_PARAMETER_MAP);
	}

	public static void setHttpPattern(ServerHttpRequest request, HttpPattern httpPattern) {
		request.setAttribute(HttpPattern.class.getName(), httpPattern);
	}

	@Nullable
	public static HttpPattern getHttpPattern(ServerHttpRequest request) {
		return (HttpPattern) request.getAttribute(HttpPattern.class.getName());
	}

	public static String decodeGETParameter(ServerHttpRequest request, String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		if (request.getMethod() != HttpMethod.GET) {
			return value;
		}

		if (StringUtils.containsChinese(value)) {
			return value;
		}

		return new CharsetCodec(request.getCharacterEncoding()).decode(CharsetCodec.ISO_8859_1.encode(value));
	}

	public static Map<String, Object> getParameterMap(ServerHttpRequest request, String appendValueChars) {
		return toSingleValueMap(request.getParameterMap(), appendValueChars,
				(value) -> decodeGETParameter(request, value));
	}

	public static Map<String, Object> toSingleValueMap(Map<String, ? extends Collection<String>> map,
			@Nullable String appendValueChars, @Nullable Decoder<String, String> decoder) {
		Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
		for (Entry<String, ? extends Collection<String>> entry : map.entrySet()) {
			Collection<String> values = entry.getValue();
			if (CollectionUtils.isEmpty(values)) {
				continue;
			}

			String[] arrays = values.toArray(new String[0]);
			if (decoder != null) {
				for (int i = 0; i < values.size(); i++) {
					arrays[i] = decoder.decode(arrays[i]);
				}
			}

			if (appendValueChars == null) {
				if (arrays.length == 1) {
					parameterMap.put(entry.getKey(), arrays[0]);
				} else {
					parameterMap.put(entry.getKey(), arrays);
				}
			} else {
				parameterMap.put(entry.getKey(),
						StringUtils.collectionToDelimitedString(Arrays.asList(arrays), appendValueChars));
			}
		}
		return parameterMap;
	}

	public static Object getRequestBody(ServerHttpRequest request) throws IOException {
		if (request.getHeaders().isJsonContentType()) {
			return JsonUtils.getSupport().parseJson(request.getReader());
		} else if (request.getHeaders().isXmlContentType()) {
			return XmlUtils.getTemplate().getParser().parse(request.getReader());
		} else if (request.getHeaders().isFormContentType()) {
			return WebUtils.getParameterMap(request, null);
		} else {
			if (request.getMethod().hasRequestBody()) {
				String content = IOUtils.read(request.getReader());
				if (StringUtils.isEmpty(content)) {
					return null;
				}

				try {
					JsonElement jsonElement = JsonUtils.getSupport().parseJson(content);
					if (jsonElement.isJsonArray() || jsonElement.isJsonObject()) {
						return jsonElement;
					}
				} catch (Exception e) {
					logger.trace(e, request.toString());
				}

				try {
					return XmlUtils.getTemplate().getParser().parse(content);
				} catch (Exception e) {
					logger.trace(e, request.toString());
				}

				try {
					MultiValueMap<String, String> valueMap = UriComponentsBuilder.newInstance().query(content).build()
							.getQueryParams();
					return WebUtils.toSingleValueMap(valueMap, null, new URLCodec(request.getCharacterEncoding()));
				} catch (Exception e) {
					logger.trace(e, request.toString());
				}
				return content;
			} else {
				return WebUtils.getParameterMap(request, null);
			}
		}
	}

	public static String getMessageId(ServerHttpRequest request, @Nullable Message output) {
		String messageId = (String) request.getAttribute(ServerHttpRequest.class.getName() + "#id");
		if (messageId == null) {
			messageId = InetUtils.getMessageId(request, output);
			request.setAttribute(ServerHttpRequest.class.getName() + "#id", messageId);
		}
		return messageId;
	}

	public static String getMessageId(HttpRequest httpRequest) {
		ServerHttpRequest request = getLocalServerHttpRequest();
		if (request == null) {
			return InetUtils.getMessageId(httpRequest, httpRequest);
		}
		return getMessageId(request, httpRequest);
	}
}
