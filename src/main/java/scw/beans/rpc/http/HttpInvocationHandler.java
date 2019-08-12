package scw.beans.rpc.http;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.beans.annotation.HttpDecoderFilter;
import scw.beans.annotation.HttpFactory;
import scw.json.JSONUtils;
import scw.net.DecoderFilter;
import scw.net.DecoderFilterChain;
import scw.net.DecoderResponse;
import scw.net.NetworkUtils;
import scw.net.http.HttpRequest;
import scw.net.support.BaseDecoderFilter;
import scw.net.support.BeanFactoryDecoderFilterChain;

public final class HttpInvocationHandler implements InvocationHandler {
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

	public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
		HttpRequest httpRequest = createHttpRequest(beanFactory, method.getDeclaringClass(), method, args);
		Collection<String> decoderNames = getDeserializerFilters(method.getDeclaringClass(), method);
		DecoderFilterChain chain = new BeanFactoryDecoderFilterChain(beanFactory, decoderNames);
		DecoderResponse response = new DecoderResponse(method.getReturnType(), chain);
		return NetworkUtils.execute(httpRequest, response);
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
		deserializerFilters.add(BaseDecoderFilter.class.getName());
		if (JSONUtils.isSupportFastJSON()) {
			deserializerFilters.add("scw.net.support.FastJsonDecoderFilter");
		}
		return deserializerFilters;
	}
}
