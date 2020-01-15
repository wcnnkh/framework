package scw.mvc.rpc.support;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.Bytes;
import scw.mvc.rpc.http.HttpRpcRequestFactory;
import scw.mvc.rpc.http.MvcRpcUtils;
import scw.net.header.HeadersConstants;
import scw.net.http.client.ClientHttpRequest;
import scw.net.http.client.accessor.HttpAccessor;
import scw.net.mime.MimeTypeUtils;
import scw.security.signature.SignatureUtils;
import scw.serializer.Serializer;

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
		final ObjectRpcRequestMessage objectRpcRequestMessage = new ObjectRpcRequestMessage(clazz, method, args);
		objectRpcRequestMessage.setAttribute("t", cts);
		objectRpcRequestMessage.setAttribute("sign",
				(SignatureUtils.byte2hex(SignatureUtils.md5(Bytes.string2bytes(cts + sign)))));
		objectRpcRequestMessage.setAttribute("responseThrowable", responseThrowable);
		ClientHttpRequest request = createRequest(new URI(host), scw.net.http.Method.POST);
		serializer.serialize(request.getBody(), objectRpcRequestMessage);
		Map<String, String> headerMap = MvcRpcUtils.getHeaderMap(shareHeaders, clazz, method);
		if (!CollectionUtils.isEmpty(headerMap)) {
			request.getHeaders().setAll(headerMap);
		}

		String ip = MvcRpcUtils.getIP();
		if (StringUtils.isNotEmpty(ip)) {
			request.getHeaders().set(HeadersConstants.X_FORWARDED_FOR, ip);
		}
		request.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
		return request;
	}

}
