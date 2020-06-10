package scw.rpc.simple.http;

import java.lang.reflect.Modifier;
import java.net.URI;

import scw.aop.Filter;
import scw.aop.ProxyInvoker;
import scw.http.client.ClientHttpRequest;
import scw.http.client.ClientHttpResponse;
import scw.http.client.accessor.HttpAccessor;
import scw.io.Bytes;
import scw.io.serialzer.Serializer;
import scw.net.MimeTypeUtils;
import scw.rpc.simple.SimpleObjectRequestMessage;
import scw.rpc.simple.SimpleResponseMessage;
import scw.security.SignatureUtils;

public class SimpleHttpObjectRpcServiceFilter extends HttpAccessor implements Filter {
	private boolean responseThrowable;
	private Serializer serializer;
	private String sign;
	private String host;

	public SimpleHttpObjectRpcServiceFilter(Serializer serializer, String sign, boolean responseThrowable,
			String host) {
		this.responseThrowable = responseThrowable;
		this.serializer = serializer;
		this.sign = sign;
		this.host = host;
	}

	public Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable {
		if (!Modifier.isAbstract(invoker.getMethod().getModifiers())) {
			return invoker.invoke(args);
		}

		long cts = System.currentTimeMillis();
		final SimpleObjectRequestMessage requestMessage = new SimpleObjectRequestMessage(invoker.getTargetClass(),
				invoker.getMethod(), args);
		requestMessage.setAttribute("t", cts);
		requestMessage.setAttribute("sign",
				(SignatureUtils.byte2hex(SignatureUtils.md5(Bytes.string2bytes(cts + sign)))));
		requestMessage.setAttribute("responseThrowable", responseThrowable);
		ClientHttpRequest request = createRequest(new URI(host), scw.http.HttpMethod.POST);
		serializer.serialize(request.getBody(), requestMessage);
		request.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
		ClientHttpResponse response = null;
		try {
			response = request.execute();
			Object obj = serializer.deserialize(response.getBody());
			if (obj instanceof SimpleResponseMessage) {
				if (((SimpleResponseMessage) obj).getError() != null) {
					throw ((SimpleResponseMessage) obj).getError();
				}
				return ((SimpleResponseMessage) obj).getResponse();
			} else {
				return obj;
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

}
