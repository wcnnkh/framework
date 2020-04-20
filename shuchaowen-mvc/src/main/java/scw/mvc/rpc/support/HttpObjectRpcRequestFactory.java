package scw.mvc.rpc.support;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.Bytes;
import scw.io.Serializer;
import scw.mvc.rpc.http.HttpRpcRequestFactory;
import scw.mvc.rpc.http.MvcRpcUtils;
import scw.net.MimeTypeUtils;
import scw.net.client.http.ClientHttpRequest;
import scw.net.client.http.accessor.HttpAccessor;
import scw.net.http.HttpHeaders;
import scw.rcp.object.ObjectRequestMessage;
import scw.security.signature.SignatureUtils;

public class HttpObjectRpcRequestFactory extends HttpAccessor implements HttpRpcRequestFactory {
	private boolean responseThrowable;
	private Serializer serializer;
	private String sign;
	private String host;
	private String[] shareHeaders;

	public HttpObjectRpcRequestFactory(Serializer serializer, String sign, boolean responseThrowable, String host,
			String[] shareHeaders) {
		this.responseThrowable = responseThrowable;
		this.serializer = serializer;
		this.sign = sign;
		this.host = host;
		this.shareHeaders = shareHeaders;
	}

	public ClientHttpRequest getHttpRequest(Class<?> clazz, Method method, Object[] args) throws Exception {
		long cts = System.currentTimeMillis();
		final ObjectRequestMessage requestMessage = new ObjectRequestMessage(clazz, method, args);
		requestMessage.setAttribute("t", cts);
		requestMessage.setAttribute("sign",
				(SignatureUtils.byte2hex(SignatureUtils.md5(Bytes.string2bytes(cts + sign)))));
		requestMessage.setAttribute("responseThrowable", responseThrowable);
		ClientHttpRequest request = createRequest(new URI(host), scw.net.http.HttpMethod.POST);
		serializer.serialize(request.getBody(), requestMessage);
		Map<String, String> headerMap = MvcRpcUtils.getHeaderMap(shareHeaders, clazz, method);
		if (!CollectionUtils.isEmpty(headerMap)) {
			request.getHeaders().setAll(headerMap);
		}

		String ip = MvcRpcUtils.getIP();
		if (StringUtils.isNotEmpty(ip)) {
			request.getHeaders().set(HttpHeaders.X_FORWARDED_FOR, ip);
		}
		request.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
		return request;
	}

}
