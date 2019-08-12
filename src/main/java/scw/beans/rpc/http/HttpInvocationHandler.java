package scw.beans.rpc.http;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.beans.annotation.HttpDeserializerFilter;
import scw.beans.annotation.HttpRequestFactory;
import scw.core.exception.NotFoundException;
import scw.core.utils.CollectionUtils;
import scw.io.DeserializerFilter;
import scw.io.DeserializerFilterChain;
import scw.io.IOUtils;
import scw.io.support.BaseDeserializerFilter;
import scw.json.JSONUtils;
import scw.net.NetworkUtils;
import scw.net.Response;
import scw.net.http.HttpRequest;

public final class HttpInvocationHandler implements InvocationHandler {
	@Autowired
	private BeanFactory beanFactory;
	private final String host;
	private LinkedList<String> defaultDeserializerFilters = new LinkedList<String>();
	private RpcRequestFactory defaultRpcRequestFactory;

	public HttpInvocationHandler(String host) {
		this.host = host;
	}

	public final void addDeserializerFilters(String name) {
		this.defaultDeserializerFilters.add(name);
	}

	public final RpcRequestFactory getDefaultRpcRequestFactory() {
		return defaultRpcRequestFactory;
	}

	public final void setDefaultRpcRequestFactory(RpcRequestFactory defaultRpcRequestFactory) {
		this.defaultRpcRequestFactory = defaultRpcRequestFactory;
	}

	public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
		HttpRequest httpRequest = createHttpRequest(beanFactory, method.getDeclaringClass(), method, args);
		return NetworkUtils.execute(httpRequest, new HttpCallResponse(method.getDeclaringClass(), method));
	}

	protected HttpRequest createHttpRequest(BeanFactory beanFactory, Class<?> clazz, Method method, Object[] args)
			throws Exception {
		HttpRequestFactory httpRequestFactory = clazz.getAnnotation(HttpRequestFactory.class);
		if (httpRequestFactory == null) {
			httpRequestFactory = method.getAnnotation(HttpRequestFactory.class);
		}

		RpcRequestFactory rpcRequestFactory = httpRequestFactory == null ? getDefaultRpcRequestFactory()
				: beanFactory.getInstance(httpRequestFactory.value());
		if (rpcRequestFactory == null) {
			rpcRequestFactory = beanFactory.getInstance(FormHttpRequestFactory.class);
		}

		return rpcRequestFactory.createHttpRequest(clazz, method, host, args);
	}

	protected Collection<String> getDeserializerFilters(Class<?> clazz, Method method) {
		LinkedList<String> deserializerFilters = new LinkedList<String>();
		HttpDeserializerFilter deserializerFilter = method.getAnnotation(HttpDeserializerFilter.class);
		if (deserializerFilter != null) {
			for (Class<? extends DeserializerFilter> filter : deserializerFilter.value()) {
				deserializerFilters.add(filter.getName());
			}
		}

		deserializerFilter = clazz.getAnnotation(HttpDeserializerFilter.class);
		if (deserializerFilter != null) {
			for (Class<? extends DeserializerFilter> filter : deserializerFilter.value()) {
				deserializerFilters.add(filter.getName());
			}

			for (String name : deserializerFilter.name()) {
				deserializerFilters.add(name);
			}
		}

		deserializerFilters.addAll(defaultDeserializerFilters);
		deserializerFilters.add(BaseDeserializerFilter.class.getName());
		if (JSONUtils.isSupportFastJSON()) {
			deserializerFilters.add("scw.io.support.FastJsonDeserializerFilter");
		}
		return deserializerFilters;
	}

	protected Object deserializer(String contentType, InputStream input, Class<?> clazz, Method method)
			throws Exception {
		HttpCallDeserializerFilterChain httpCallDeserializerFilterChain = new HttpCallDeserializerFilterChain(clazz,
				method);
		return httpCallDeserializerFilterChain.doDeserialize(method.getReturnType(), input);
	}

	private final class HttpCallResponse implements Response<Object> {
		private Method method;
		private Class<?> clazz;

		public HttpCallResponse(Class<?> clazz, Method method) {
			this.method = method;
			this.clazz = clazz;
		}

		public Object response(URLConnection urlConnection) throws Throwable {
			if(urlConnection.getDoInput()){
				return null;
			}
			
			InputStream input = null;
			try {
				input = urlConnection.getInputStream();
				return deserializer(urlConnection.getContentType(), input, clazz, method);
			} finally {
				IOUtils.close(input);
			}
		}

	}

	private final class HttpCallDeserializerFilterChain implements DeserializerFilterChain {
		private Iterator<String> iterator;

		public HttpCallDeserializerFilterChain(Class<?> clazz, Method method) {
			Collection<String> classes = getDeserializerFilters(clazz, method);
			if (!CollectionUtils.isEmpty(classes)) {
				this.iterator = classes.iterator();
			}
		}

		public Object doDeserialize(Class<?> type, InputStream input) throws IOException {
			if (iterator == null) {
				throw new NotFoundException("not found DeserializerFilter：" + type);
			}

			if (iterator.hasNext()) {
				DeserializerFilter deserializerFilter = beanFactory.getInstance(iterator.next());
				return deserializerFilter.deserialize(type, input, this);
			}
			throw new NotFoundException("not found DeserializerFilter：" + type);
		}
	}
}
