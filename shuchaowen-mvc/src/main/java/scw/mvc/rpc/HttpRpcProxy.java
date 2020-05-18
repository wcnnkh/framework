package scw.mvc.rpc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.core.instance.InstanceFactory;
import scw.http.client.ClientHttpRequest;
import scw.http.client.ClientHttpResponse;
import scw.mvc.rpc.annotation.MessageConvert;
import scw.net.NetworkUtils;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.value.property.PropertyFactory;

public class HttpRpcProxy extends MultiMessageConverter implements Filter {
	private static final long serialVersionUID = 1L;
	private final HttpRpcRequestFactory httpRpcRequestFactory;
	private final InstanceFactory instanceFactory;
	private MessageConverter messageConverter;

	public HttpRpcProxy(PropertyFactory propertyFactory, InstanceFactory instanceFactory,
			HttpRpcRequestFactory httpRpcRequestFactory) {
		this.instanceFactory = instanceFactory;
		this.httpRpcRequestFactory = httpRpcRequestFactory;
		addAll(NetworkUtils.getMessageConverters());
	}

	private void appendMessageConvert(Collection<MessageConverter> messageConverters, MessageConvert messageConvert) {
		for (String name : messageConvert.name()) {
			if (instanceFactory.isInstance(name)) {
				messageConverters.add((MessageConverter) instanceFactory.getInstance(name));
			}
		}

		for (Class<?> clazz : messageConvert.value()) {
			if (instanceFactory.isInstance(clazz)) {
				messageConverters.add((MessageConverter) instanceFactory.getInstance(clazz));
			}
		}
	}

	protected MultiMessageConverter getMessageConverter(Class<?> clazz, Method method) {
		MultiMessageConverter converters = new MultiMessageConverter();
		converters.add(this);
		MessageConvert messageConvert = method.getAnnotation(MessageConvert.class);
		if (messageConvert != null) {
			appendMessageConvert(converters, messageConvert);
		}

		messageConvert = method.getDeclaringClass().getAnnotation(MessageConvert.class);
		if (messageConvert != null) {
			appendMessageConvert(converters, messageConvert);
		}

		if (messageConvert != null) {
			converters.add(messageConverter);
		}
		return converters;
	}

	public Object doFilter(Invoker invoker, ProxyContext context, FilterChain filterChain) throws Throwable {
		if (Modifier.isAbstract(context.getMethod().getModifiers())
				|| Modifier.isInterface(context.getMethod().getModifiers())) {
			ClientHttpRequest request = httpRpcRequestFactory.getHttpRequest(context.getTargetClass(),
					context.getMethod(), context.getArgs());
			ClientHttpResponse httpInputMessage = request.execute();
			return getMessageConverter(context.getTargetClass(), context.getMethod())
					.read(context.getMethod().getGenericReturnType(), httpInputMessage);
		}
		return filterChain.doFilter(invoker, context);
	}
}
