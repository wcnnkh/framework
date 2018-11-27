package shuchaowen.core.http.rpc;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.proxy.ProxyFactory;
import shuchaowen.core.http.rpc.serialization.JavaObjectSerializer;
import shuchaowen.core.http.rpc.serialization.Serializer;

public class HttpConsumer implements ProxyFactory{
	private volatile Map<Class<?>, Object> proxyMap = new HashMap<Class<?>, Object>();
	
	private String host;
	private Charset charset;
	private String signStr;
	private Serializer serializer;
	
	public HttpConsumer(String host, String signStr) {
		this(host, signStr, new JavaObjectSerializer(), Charset.forName("UTF-8"));
	}
	
	public HttpConsumer(String host, String signStr, Charset charset) {
		this(host, signStr, new JavaObjectSerializer(), charset);
	}
	
	public HttpConsumer(String host, String signStr, Serializer serializer, Charset charset) {
		this.host = host;
		this.charset = charset;
		this.serializer = serializer;
		this.signStr = signStr;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(BeanFactory beanFactory, Class<T> interfaceClass) throws Exception {
		Object obj = proxyMap.get(interfaceClass);
		if(obj == null){
			synchronized (proxyMap) {
				obj = proxyMap.get(interfaceClass);
				if(obj == null){
					Bean bean = new HttpRPCBean(interfaceClass, host, signStr, serializer, charset);
					obj = bean.newInstance();
					proxyMap.put(interfaceClass, obj);
				}
			}
		}
		return (T) obj;
	}
}
