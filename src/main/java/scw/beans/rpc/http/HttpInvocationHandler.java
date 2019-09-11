package scw.beans.rpc.http;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.beans.rpc.http.annotation.HttpDecoderFilter;
import scw.beans.rpc.http.annotation.HttpFactory;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.net.DecoderFilter;
import scw.net.DecoderFilterChain;
import scw.net.DecoderResponse;
import scw.net.NetworkUtils;
import scw.net.http.HttpRequest;
import scw.net.support.BeanFactoryDecoderFilterChain;
import scw.net.support.DefaultDecoderFilter;

public final class HttpInvocationHandler implements Filter {
	@Autowired
	private BeanFactory beanFactory;
	private final String host;
	private LinkedList<String> defaultDeserializerFilters = new LinkedList<String>();
	private RequestFactory defaultRpcRequestFactory;

	public HttpInvocationHandler(String host) {
		this.host = host;
	}

	public final void addDeserializerFilters(String name) {
		this.defaultDeserializerFilters.add(name);
	}

	public final RequestFactory getDefaultRpcRequestFactory() {
		return defaultRpcRequestFactory;
	}

	public final void setDefaultRpcRequestFactory(RequestFactory defaultRpcRequestFactory) {
		this.defaultRpcRequestFactory = defaultRpcRequestFactory;
	}

	protected HttpRequest createHttpRequest(BeanFactory beanFactory, Class<?> clazz, Method method, Object[] args)
			throws Exception {
		HttpFactory httpFactory = clazz.getAnnotation(HttpFactory.class);
		if (httpFactory == null) {
			httpFactory = method.getAnnotation(HttpFactory.class);
		}

		RequestFactory requestFactory = httpFactory == null ? getDefaultRpcRequestFactory()
				: beanFactory.getInstance(httpFactory.value());
		if (requestFactory == null) {
			requestFactory = beanFactory.getInstance(FormHttpRequestFactory.class);
		}

		return requestFactory.createHttpRequest(clazz, method, host, args);
	}

	protected Collection<String> getDeserializerFilters(Class<?> clazz, Method method) {
		LinkedList<String> deserializerFilters = new LinkedList<String>();
		HttpDecoderFilter deserializerFilter = method.getAnnotation(HttpDecoderFilter.class);
		if (deserializerFilter != null) {
			for (Class<? extends DecoderFilter> filter : deserializerFilter.value()) {
				deserializerFilters.add(filter.getName());
			}
		}

		deserializerFilter = clazz.getAnnotation(HttpDecoderFilter.class);
		if (deserializerFilter != null) {
			for (Class<? extends DecoderFilter> filter : deserializerFilter.value()) {
				deserializerFilters.add(filter.getName());
			}

			for (String name : deserializerFilter.name()) {
				deserializerFilters.add(name);
			}
		}

		deserializerFilters.addAll(defaultDeserializerFilters);
		deserializerFilters.add(DefaultDecoderFilter.class.getName());
		return deserializerFilters;
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		HttpRequest httpRequest = createHttpRequest(beanFactory, method.getDeclaringClass(), method, args);
		Collection<String> decoderNames = getDeserializerFilters(method.getDeclaringClass(), method);
		DecoderFilterChain chain = new BeanFactoryDecoderFilterChain(beanFactory, decoderNames);
		DecoderResponse response = new DecoderResponse(method.getGenericReturnType(), chain);
		return NetworkUtils.execute(httpRequest, response);
	}
}
