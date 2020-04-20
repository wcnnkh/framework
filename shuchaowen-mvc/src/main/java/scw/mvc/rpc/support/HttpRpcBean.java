package scw.mvc.rpc.support;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.io.ObjectMessageConveter;
import scw.io.Serializer;
import scw.mvc.rpc.http.HttpRpcFactory;
import scw.mvc.rpc.http.HttpRpcRequestFactory;
import scw.net.message.converter.support.AllMessageConverter;
import scw.util.value.property.PropertyFactory;

public final class HttpRpcBean extends AbstractInterfaceBeanDefinition {
	private HttpRpcFactory rpcFactory;

	public HttpRpcBean(BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, String host, String signStr, Serializer serializer, boolean responsethrowable,
			String[] shareHeaders) {
		super(beanFactory, propertyFactory, type);
		HttpRpcRequestFactory httpRpcRequestFactory = new HttpObjectRpcRequestFactory(serializer, signStr,
				responsethrowable, host, shareHeaders);
		this.rpcFactory = new HttpRpcFactory(beanFactory, propertyFactory, httpRpcRequestFactory);
		rpcFactory.add(new ObjectMessageConveter(serializer));
		rpcFactory.add(new AllMessageConverter());
		init();
	}

	public Object create() {	
		return rpcFactory.getProxy(getTargetClass());
	}
}
