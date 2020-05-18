package scw.rpc.http;

import java.lang.reflect.Modifier;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.beans.BeanFactory;
import scw.core.Constants;
import scw.core.instance.InstanceUtils;
import scw.http.client.ClientHttpRequest;
import scw.http.client.ClientHttpResponse;
import scw.lang.NotSupportedException;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.value.property.PropertyFactory;

public class HttpRpcProxyFilter implements Filter {
	private final HttpRpcProxyRequestFactory httpRpcProxyRequestFactory;
	private final MessageConverter messageConverter;

	public HttpRpcProxyFilter(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.httpRpcProxyRequestFactory = beanFactory.isInstance(HttpRpcProxyRequestFactory.class)
				? beanFactory.getInstance(HttpRpcProxyRequestFactory.class)
				: new RestfulHttpRpcProxyRequestFactory(propertyFactory, Constants.DEFAULT_CHARSET_NAME);
		MultiMessageConverter messageConverter = new MultiMessageConverter();
		messageConverter
				.addAll(InstanceUtils.getConfigurationList(MessageConverter.class, beanFactory, propertyFactory));
		this.messageConverter = messageConverter;
	}

	public HttpRpcProxyFilter(HttpRpcProxyRequestFactory httpRpcProxyRequestFactory,
			MessageConverter messageConverter) {
		this.httpRpcProxyRequestFactory = httpRpcProxyRequestFactory;
		this.messageConverter = messageConverter;
	}

	public Object doFilter(Invoker invoker, ProxyContext context, FilterChain filterChain) throws Throwable {
		if (Modifier.isAbstract(context.getMethod().getModifiers())
				|| Modifier.isInterface(context.getMethod().getModifiers())) {
			ClientHttpRequest request = httpRpcProxyRequestFactory.getClientHttpRequest(context);
			if (request != null) {
				ClientHttpResponse response = null;
				try {
					response = request.execute();
					if (!messageConverter.canRead(context.getMethod().getGenericReturnType(),
							response.getContentType())) {
						throw new NotSupportedException("type=" + context.getMethod().getGenericReturnType().toString()
								+ ", contentType=" + response.getContentType());
					}

					return messageConverter.read(context.getMethod().getGenericReturnType(), response);
				} finally {
					if (response != null) {
						response.close();
					}
				}
			}
		}
		return invoker.invoke(context.getArgs());
	}

}
