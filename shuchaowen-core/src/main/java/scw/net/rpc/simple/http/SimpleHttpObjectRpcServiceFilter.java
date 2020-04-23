package scw.net.rpc.simple.http;

import java.lang.reflect.Modifier;
import java.net.URI;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.io.Bytes;
import scw.io.Serializer;
import scw.net.MimeTypeUtils;
import scw.net.client.http.ClientHttpRequest;
import scw.net.client.http.accessor.HttpAccessor;
import scw.net.rpc.simple.SimpleObjectRequestMessage;
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

	public Object doFilter(Invoker invoker, Context context, FilterChain filterChain) throws Throwable {
		if (!Modifier.isAbstract(context.getMethod().getModifiers())) {
			return filterChain.doFilter(invoker, context);
		}

		long cts = System.currentTimeMillis();
		final SimpleObjectRequestMessage requestMessage = new SimpleObjectRequestMessage(context.getTargetClass(),
				context.getMethod(), context.getArgs());
		requestMessage.setAttribute("t", cts);
		requestMessage.setAttribute("sign",
				(SignatureUtils.byte2hex(SignatureUtils.md5(Bytes.string2bytes(cts + sign)))));
		requestMessage.setAttribute("responseThrowable", responseThrowable);
		ClientHttpRequest request = createRequest(new URI(host), scw.net.http.HttpMethod.POST);
		serializer.serialize(request.getBody(), requestMessage);
		request.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
		return request;
	}

}
