package scw.mvc.rpc.http;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.mvc.rpc.annotation.MessageConvert;
import scw.net.NetworkUtils;
import scw.net.http.client.ClientHttpRequest;
import scw.net.http.client.ClientHttpResponse;
import scw.net.message.converter.MessageConverter;
import scw.rcp.object.ObjectResponseMessage;

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

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		if (Modifier.isAbstract(method.getModifiers()) || Modifier.isInterface(method.getModifiers())) {
			ClientHttpRequest request = httpRpcRequestFactory.getHttpRequest(targetClass, method, args);
			ClientHttpResponse httpInputMessage = request.execute();
			Object obj = NetworkUtils.read(method.getGenericReturnType(), httpInputMessage,
					getMessageConverters(method.getDeclaringClass(), method));
			if (obj instanceof ObjectResponseMessage) {
				if (((ObjectResponseMessage) obj).getError() != null) {
					throw ((ObjectResponseMessage) obj).getError();
				}
				return ((ObjectResponseMessage) obj).getResponse();
			} else {
				return obj;
			}
		}
		return filterChain.doFilter(invoker, proxy, targetClass, method, args);
	}

}
