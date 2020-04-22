package scw.mvc.rpc.http;

import scw.beans.BeanFactory;
import scw.mvc.rpc.RPCProxyFactory;
import scw.util.value.property.PropertyFactory;

public class HttpRpcFactory extends HttpRpcProxy implements RPCProxyFactory {
	private static final long serialVersionUID = 1L;
	private final BeanFactory beanFactory;

	public HttpRpcFactory(BeanFactory beanFactory,
			PropertyFactory propertyFactory,
			HttpRpcRequestFactory httpRpcRequestFactory) {
		super(propertyFactory, beanFactory, httpRpcRequestFactory);
		this.beanFactory = beanFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> clazz) {
		return (T) beanFactory.getAop().proxy(clazz, null, this).create();
	}

}
