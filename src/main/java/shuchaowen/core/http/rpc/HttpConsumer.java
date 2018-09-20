package shuchaowen.core.http.rpc;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.client.Response;
import shuchaowen.core.http.client.method.HttpPost;
import shuchaowen.core.http.rpc.serialization.JavaObjectSerializer;
import shuchaowen.core.http.rpc.serialization.Serializer;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.util.LazyMap;
import shuchaowen.core.util.SignHelp;

public class HttpConsumer implements Consumer{
	private LazyMap<String, Object> proxyMap = new LazyMap<String, Object>();
	
	/**
	 * rpc服务的地址
	 */
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
	public <T> T getService(final Class<T> interfaceClass) {
		return (T) proxyMap.get(interfaceClass.getName(), new Callable<Object>() {

			public Object call() throws Exception {
				Object newProxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
						new Class[] { interfaceClass }, new InvocationHandler() {

							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								HttpConsumerInvoker httpConsumerInvoker = new HttpConsumerInvoker(host, method, signStr, serializer, charset);
								return httpConsumerInvoker.invoke(args);
							}
						});
				return newProxyInstance;
			}
		});
	}
}

class HttpConsumerInvoker implements Invoker{
	private Method method;
	private String host;
	private Type returnType;
	private Charset charset;
	private String signStr;
	private Serializer serializer;
	
	public HttpConsumerInvoker(String host,Method method, String signStr, Serializer serializer, Charset charset){
		this.method = method;
		this.host = host;
		this.returnType = method.getGenericReturnType();
		this.charset = charset;
		this.serializer = serializer;
		this.signStr = signStr;
	}
	
	public Object invoke(Object... args) throws Exception{
		long cts = System.currentTimeMillis();
		Message message = new Message(method, args);
		message.setAttribute("t", cts);
		message.setAttribute("sign", SignHelp.md5Str(cts + signStr, charset.name()));
		HttpPost httpPost = new HttpPost(host);
		httpPost.setRequestProperties("Content-Type","application/x-java-serialized-object");
		ObjectParameter objectParamter = new ObjectParameter(serializer, message);
		httpPost.addParam(objectParamter);
		Response response = null;
		Object obj;
		InputStream in;
		try {
			response = httpPost.execute();
			in = response.getInputStream();
			obj = serializer.decode(in, returnType);
			return obj;
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}finally {
			if(response != null){
				response.disconnect();
			}
		}
	}
}