package scw.rpc.http;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.rpc.RpcFactory;

public class HttpRpcFactory extends HttpRpcProxy implements RpcFactory {
	private static final long serialVersionUID = 1L;
	private final BeanFactory beanFactory;

	public HttpRpcFactory(BeanFactory beanFactory, PropertyFactory propertyFactory,
			HttpRpcRequestFactory httpRpcRequestFactory) {
		super(propertyFactory, beanFactory, httpRpcRequestFactory);
		this.beanFactory = beanFactory;
	}

	public <T> T getProxy(Class<T> clazz) {
		return (T) BeanUtils.proxyInterface(beanFactory, clazz, this);
	}

}
