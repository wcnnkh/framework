package scw.beans.rpc;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.io.serializer.Serializer;
import scw.rpc.http.HttpRpcFactory;
import scw.rpc.http.HttpRpcRequestFactory;
import scw.rpc.support.HttpObjectRpcRequestFactory;
import scw.rpc.support.ObjectRpcMessageConvert;

public final class HttpRpcBean extends AbstractInterfaceBeanDefinition {
	private HttpRpcFactory rpcFactory;

	public HttpRpcBean(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, String host, String signStr, Serializer serializer, boolean responsethrowable,
			String[] shareHeaders) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		HttpRpcRequestFactory httpRpcRequestFactory = new HttpObjectRpcRequestFactory(serializer, signStr,
				responsethrowable, host, shareHeaders);
		this.rpcFactory = new HttpRpcFactory(beanFactory, propertyFactory, httpRpcRequestFactory);
		rpcFactory.add(new ObjectRpcMessageConvert(serializer, Constants.DEFAULT_CHARSET_NAME));
		init();
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		return (T) rpcFactory.getProxy(getType());
	}
}
