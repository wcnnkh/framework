package scw.mvc.rpc.http;

import java.util.Arrays;

import scw.aop.Filter;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.mvc.rpc.RpcFactory;

public class HttpRpcFactory extends HttpRpcProxy implements RpcFactory {
	private static final long serialVersionUID = 1L;
	private final BeanFactory beanFactory;

	public HttpRpcFactory(BeanFactory beanFactory, PropertyFactory propertyFactory,
			HttpRpcRequestFactory httpRpcRequestFactory) {
		super(propertyFactory, beanFactory, httpRpcRequestFactory);
		this.beanFactory = beanFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> clazz) {
		return (T) BeanUtils.createProxy(beanFactory, clazz, null, Arrays.asList((Filter) this)).create();
	}

}
