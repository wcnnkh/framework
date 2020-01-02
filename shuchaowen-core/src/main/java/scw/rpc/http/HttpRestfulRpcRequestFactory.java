package scw.rpc.http;

import java.lang.reflect.Method;
import java.util.Map;

import scw.core.PropertyFactory;
import scw.core.StringFormat;
import scw.core.annotation.Host;
import scw.core.reflect.AnnotationUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.StringUtils;
import scw.net.http.HttpRequest;
import scw.net.http.HttpUtils;
import scw.net.mime.MimeTypeConstants;
import scw.net.mime.SimpleMimeType;
import scw.rpc.annotation.RequestContentType;
import scw.rpc.annotation.RequestContentType.ContentType;
import scw.util.KeyValuePair;

public class HttpRestfulRpcRequestFactory implements HttpRpcRequestFactory {
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

	public final HttpRequest getHttpRequest(Class<?> clazz, Method method, Object[] args) throws Exception {
		String host;
		Host h = AnnotationUtils.getAnnotation(Host.class, clazz, method);
		host = h == null? this.host:h.value();
		if (StringUtils.isEmpty(host)) {
			host = "http://127.0.0.1";
		}

		KeyValuePair<scw.net.http.Method, String> requestMethod = AnnotationUtils.getHttpMethodAnnotation(method);
		scw.net.http.Method httpMethod = scw.net.http.Method.GET;
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
			public String getProperty(String key) {
				Object value = parameterMap.remove(key);
				return value == null ? null : value.toString();
			};
		};

		host = stringFormat.format(host);
		host = FormatUtils.format(host, propertyFactory);

		host = httpMethod == scw.net.http.Method.GET ? HttpUtils.appendParameters(host, parameterMap, charsetName)
				: host;

		HttpRestfulRpcRequest httpRestfulRpcRequest = new HttpRestfulRpcRequest(httpMethod, host, charsetName);
		Map<String, String> headerMap = MvcRpcUtils.getHeaderMap(shareHeaders, clazz, method);
		if (!CollectionUtils.isEmpty(headerMap)) {
			httpRestfulRpcRequest.setRequestProperties(headerMap);
		}

		RequestContentType requestContentType = AnnotationUtils.getAnnotation(RequestContentType.class, clazz, method);
		if (requestContentType != null) {
			if (requestContentType.value() == ContentType.FORM) {
				httpRestfulRpcRequest.setContentType(
						new SimpleMimeType(MimeTypeConstants.APPLICATION_X_WWW_FORM_URLENCODED, charsetName));
			} else if (requestContentType.value() == ContentType.JSON) {
				httpRestfulRpcRequest
						.setContentType(new SimpleMimeType(MimeTypeConstants.APPLICATION_JSON, charsetName));
			}
		}
		return httpRestfulRpcRequest;
	}
}
