package shuchaowen.beans.rpc.http;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

import shuchaowen.beans.AbstractInterfaceProxyBean;
import shuchaowen.common.io.decoder.JavaObjectDecoder;
import shuchaowen.common.reflect.Invoker;
import shuchaowen.common.utils.SignUtils;
import shuchaowen.connection.http.HttpPOST;
import shuchaowen.connection.http.entity.JavaObjectRequestEntity;

public class HttpRPCBean extends AbstractInterfaceProxyBean{
	private final String host;
	private final String signStr;
	private final Charset charset;
	
	public HttpRPCBean(Class<?> interfaceClass, String host, String signStr, Charset charset) throws Exception{
		super(interfaceClass);
		this.host = host;
		this.signStr = signStr;
		this.charset = charset;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		Object newProxyInstance = Proxy.newProxyInstance(getType().getClassLoader(),
				new Class[] { getType() }, new InvocationHandler() {

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						HttpConsumerInvoker httpConsumerInvoker = new HttpConsumerInvoker(host, method, signStr, charset);
						return httpConsumerInvoker.invoke(args);
					}
				});
		return (T) newProxyInstance;
	}
}

class HttpConsumerInvoker implements Invoker{
	private Method method;
	private String host;
	private Charset charset;
	private String signStr;
	
	public HttpConsumerInvoker(String host, Method method, String signStr, Charset charset){
		this.method = method;
		this.host = host;
		this.charset = charset;
		this.signStr = signStr;
	}
	
	public Object invoke(Object... args) throws Exception{
		long cts = System.currentTimeMillis();
		Message message = new Message(method, args);
		message.setAttribute("t", cts);
		message.setAttribute("sign", SignUtils.md5Str(cts + signStr, charset.name()));
		HttpPOST http = null;
		JavaObjectRequestEntity requestEntity = new JavaObjectRequestEntity();
		requestEntity.add(message);
		try {
			http = new HttpPOST(host);
			http.setRequestEntity(requestEntity);
			return http.execute(JavaObjectDecoder.DECODER);
		} finally {
			if(http != null){
				http.disconnect();
			}
		}
	}
}
