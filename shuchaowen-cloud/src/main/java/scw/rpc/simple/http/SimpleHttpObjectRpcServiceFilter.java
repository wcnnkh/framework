package scw.rpc.simple.http;

import java.lang.reflect.Modifier;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.MethodInvoker;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.io.Bytes;
import scw.io.serialzer.Serializer;
import scw.rpc.simple.SimpleObjectRequestMessage;
import scw.rpc.simple.SimpleResponseMessage;
import scw.security.SignatureUtils;

public class SimpleHttpObjectRpcServiceFilter implements Filter {
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

	public Object doFilter(MethodInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable {
		if (!Modifier.isAbstract(invoker.getMethod().getModifiers())) {
			return filterChain.doFilter(invoker, args);
		}

		long cts = System.currentTimeMillis();
		final SimpleObjectRequestMessage requestMessage = new SimpleObjectRequestMessage(invoker.getSourceClass(),
				invoker.getMethod(), args);
		requestMessage.setAttribute("t", cts);
		requestMessage.setAttribute("sign",
				(SignatureUtils.byte2hex(SignatureUtils.md5(Bytes.string2bytes(cts + sign)))));
		requestMessage.setAttribute("responseThrowable", responseThrowable);
		byte[] body = serializer.serialize(requestMessage);
		byte[] response = HttpUtils.getHttpClient().post(byte[].class, host, body, MediaType.APPLICATION_OCTET_STREAM).getBody();
		Object obj = serializer.deserialize(response);
		if (obj instanceof SimpleResponseMessage) {
			if (((SimpleResponseMessage) obj).getError() != null) {
				throw ((SimpleResponseMessage) obj).getError();
			}
			return ((SimpleResponseMessage) obj).getResponse();
		} else {
			return obj;
		}
	}

}
