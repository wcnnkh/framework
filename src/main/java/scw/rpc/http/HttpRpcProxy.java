package scw.rpc.http;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.instance.InstanceFactory;
import scw.net.HttpMessage;
import scw.net.MessageConverter;
import scw.net.http.HttpRequest;
import scw.rpc.annotation.MessageConvert;

public class HttpRpcProxy extends LinkedList<MessageConverter> implements Filter {
	private static final long serialVersionUID = 1L;
	private final HttpRpcRequestFactory httpRpcRequestFactory;
	private final InstanceFactory instanceFactory;
	private MessageConverter messageConverter;

	public HttpRpcProxy(PropertyFactory propertyFactory, InstanceFactory instanceFactory,
			HttpRpcRequestFactory httpRpcRequestFactory) {
		this.instanceFactory = instanceFactory;
		this.httpRpcRequestFactory = httpRpcRequestFactory;
		BeanUtils.appendBean(this, instanceFactory, propertyFactory, MessageConverter.class, "rpc.message.convert");
		if (instanceFactory.isSingleton(MessageConverter.class) && instanceFactory.isInstance(MessageConverter.class)) {
			messageConverter = instanceFactory.getInstance(MessageConverter.class);
		}
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

	protected Collection<MessageConverter> getMessageConverters(Class<?> clazz, Method method) {
		LinkedList<MessageConverter> converters = new LinkedList<MessageConverter>();
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
		converters.addAll(this);
		return converters;
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (Modifier.isAbstract(method.getModifiers()) || Modifier.isInterface(method.getModifiers())) {
			HttpRequest httpRequest = httpRpcRequestFactory.getHttpRequest(method.getDeclaringClass(), method, args);
			HttpMessage httpMessage = httpRequest.execute();
			return httpMessage.convert(getMessageConverters(method.getDeclaringClass(), method),
					method.getGenericReturnType());
		}
		return filterChain.doFilter(invoker, proxy, method, args);
	}

}