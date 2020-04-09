package scw.mvc.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import scw.core.StringFormat;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.mvc.rpc.annotation.Host;
import scw.mvc.rpc.annotation.RequestContentType;
import scw.mvc.rpc.annotation.RequestContentType.ContentType;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.client.http.ClientHttpRequest;
import scw.net.client.http.HttpUtils;
import scw.net.client.http.accessor.HttpAccessor;
import scw.util.FormatUtils;
import scw.util.KeyValuePair;
import scw.util.value.property.PropertyFactory;

public class HttpRestfulRpcRequestFactory extends HttpAccessor implements HttpRpcRequestFactory {
	private String charsetName;
	private String host;
	private PropertyFactory propertyFactory;
	private String[] shareHeaders;

	public HttpRestfulRpcRequestFactory(PropertyFactory propertyFactory, String host, String charsetName,
			String[] shareHeaders) {
		this.charsetName = charsetName;
		this.host = host;
		this.propertyFactory = propertyFactory;
		this.shareHeaders = shareHeaders;
	}

	public final String getCharsetName() {
		return charsetName;
	}

	public final void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public final ClientHttpRequest getHttpRequest(Class<?> clazz, Method method, Object[] args) throws Exception {
		String host;
		Host h = AnnotationUtils.getAnnotation(Host.class, clazz, method);
		host = h == null ? this.host : h.value();
		if (StringUtils.isEmpty(host)) {
			host = "http://127.0.0.1";
		}

		KeyValuePair<scw.net.http.HttpMethod, String> requestMethod = AnnotationUtils.getHttpMethodAnnotation(method);
		scw.net.http.HttpMethod httpMethod = scw.net.http.HttpMethod.GET;
		if (requestMethod != null) {
			httpMethod = requestMethod.getKey();
			if (StringUtils.isNotEmpty(requestMethod.getValue())) {
				if (requestMethod.getValue().startsWith("/")) {
					host = host + requestMethod.getValue();
				} else {
					host = host + "/" + requestMethod.getValue();
				}
			}
		}

		final Map<String, Object> parameterMap = MvcRpcUtils.getParameterMap(method, args, true);
		StringFormat stringFormat = new StringFormat("{", "}") {
			public String getValue(String key) {
				Object value = parameterMap.remove(key);
				return value == null ? null : value.toString();
			};
		};

		host = stringFormat.format(host);
		host = FormatUtils.format(host, propertyFactory);

		host = httpMethod == scw.net.http.HttpMethod.GET ? HttpUtils.appendParameters(host, parameterMap, charsetName)
				: host;

		ClientHttpRequest clientHttpRequest = createRequest(new URI(host), httpMethod);
		RequestContentType requestContentType = AnnotationUtils.getAnnotation(RequestContentType.class, clazz, method);
		if (requestContentType != null) {
			if (requestContentType.value() == ContentType.FORM) {
				clientHttpRequest
						.setContentType(new MimeType(MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
			} else if (requestContentType.value() == ContentType.JSON) {
				clientHttpRequest.setContentType(new MimeType(MimeTypeUtils.APPLICATION_JSON, charsetName));
			}
		}

		Map<String, String> headerMap = MvcRpcUtils.getHeaderMap(shareHeaders, clazz, method);
		if (!CollectionUtils.isEmpty(headerMap)) {
			clientHttpRequest.getHeaders().setAll(headerMap);
		}
		return clientHttpRequest;
	}
}
