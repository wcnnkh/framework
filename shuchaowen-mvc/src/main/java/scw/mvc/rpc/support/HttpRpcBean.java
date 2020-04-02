package scw.mvc.rpc.support;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.mvc.rpc.http.HttpRpcFactory;
import scw.mvc.rpc.http.HttpRpcRequestFactory;
import scw.net.message.converter.support.AllMessageConverter;
import scw.serializer.ObjectMessageConveter;
import scw.serializer.Serializer;
import scw.util.value.property.PropertyFactory;

public final class HttpRpcBean extends AbstractInterfaceBeanDefinition {
	private HttpRpcFactory rpcFactory;

	public HttpRpcBean(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, String host, String signStr, Serializer serializer, boolean responsethrowable,
			String[] shareHeaders) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		HttpRpcRequestFactory httpRpcRequestFactory = new HttpObjectRpcRequestFactory(serializer, signStr,
				responsethrowable, host, shareHeaders);
		this.rpcFactory = new HttpRpcFactory(beanFactory, propertyFactory, httpRpcRequestFactory);
		rpcFactory.add(new ObjectMessageConveter(serializer));
		rpcFactory.add(new AllMessageConverter());
		init();
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		return (T) rpcFactory.getProxy(getType());
	}
}
