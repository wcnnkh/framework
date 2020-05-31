package scw.rpc.http;

import java.net.URI;
import java.util.Map;

import scw.aop.ProxyInvoker;
import scw.core.StringFormat;
import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpUtils;
import scw.http.client.ClientHttpRequest;
import scw.http.client.accessor.HttpAccessor;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.rpc.http.annotation.HttpClient;
import scw.rpc.http.annotation.HttpClient.ContentType;
import scw.util.FormatUtils;
import scw.util.KeyValuePair;
import scw.value.property.PropertyFactory;

public class RestfulHttpRpcProxyRequestFactory extends HttpAccessor implements HttpRpcProxyRequestFactory {
	private String charsetName;
	private PropertyFactory propertyFactory;

	public RestfulHttpRpcProxyRequestFactory(PropertyFactory propertyFactory, String charsetName) {
		this.charsetName = charsetName;
		this.propertyFactory = propertyFactory;
	}

	public final String getCharsetName() {
		return charsetName;
	}

	public final void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public ClientHttpRequest getClientHttpRequest(ProxyInvoker invoker, Object[] args) throws Exception {
		HttpClient rpc = AnnotationUtils.getAnnotation(HttpClient.class, invoker.getTargetClass(),
				invoker.getMethod());
		if (rpc == null) {
			return null;
		}
		String host = rpc.value();
		if (StringUtils.isEmpty(host)) {
			return null;
		}

		KeyValuePair<scw.http.HttpMethod, String> requestMethod = AnnotationUtils
				.getHttpMethodAnnotation(invoker.getMethod());
		scw.http.HttpMethod httpMethod = scw.http.HttpMethod.GET;
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

		final Map<String, Object> parameterMap = ParameterUtils.getParameterMap(invoker.getMethod(),
				args);
		StringFormat stringFormat = new StringFormat("{", "}") {
			public String getValue(String key) {
				Object value = parameterMap.remove(key);
				return value == null ? null : value.toString();
			};
		};

		host = stringFormat.format(host);
		host = FormatUtils.format(host, propertyFactory);

		host = httpMethod == scw.http.HttpMethod.GET ? HttpUtils.appendParameters(host, parameterMap, charsetName)
				: host;

		ClientHttpRequest clientHttpRequest = createRequest(new URI(host), httpMethod);
		if (rpc.requestContentType() == scw.rpc.http.annotation.HttpClient.ContentType.FORM) {
			clientHttpRequest
					.setContentType(new MimeType(MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
		} else if (rpc.requestContentType() == ContentType.JSON) {
			clientHttpRequest.setContentType(new MimeType(MimeTypeUtils.APPLICATION_JSON, charsetName));
		}
		return clientHttpRequest;
	}

}
