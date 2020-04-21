package scw.mvc.rpc.support;

import scw.beans.AbstractBeanBuilder;
import scw.beans.BeanFactory;
import scw.io.ObjectMessageConveter;
import scw.io.Serializer;
import scw.mvc.rpc.http.HttpRpcFactory;
import scw.mvc.rpc.http.HttpRpcRequestFactory;
import scw.net.message.converter.support.AllMessageConverter;
import scw.util.value.property.PropertyFactory;

public class HttpRpcBeanBuilder extends AbstractBeanBuilder{
	private HttpRpcFactory rpcFactory;
	
	public HttpRpcBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass, String host, String signStr, Serializer serializer, boolean responsethrowable,
			String[] shareHeaders) {
		super(beanFactory, propertyFactory, targetClass);
		HttpRpcRequestFactory httpRpcRequestFactory = new HttpObjectRpcRequestFactory(serializer, signStr,
				responsethrowable, host, shareHeaders);
		this.rpcFactory = new HttpRpcFactory(beanFactory, propertyFactory, httpRpcRequestFactory);
		rpcFactory.add(new ObjectMessageConveter(serializer));
		rpcFactory.add(new AllMessageConverter());
	}
	
	public boolean isInstance() {
		return true;
	}

	public Object create() {	
		return rpcFactory.getProxy(getTargetClass());
	}
}
