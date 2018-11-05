package shuchaowen.core.http.rpc;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

import shuchaowen.core.beans.AnnotationBean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.exception.NotSupportException;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.client.Response;
import shuchaowen.core.http.client.method.HttpPost;
import shuchaowen.core.http.rpc.serialization.Serializer;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.util.SignHelp;

public class HttpRPCBean extends AnnotationBean{
	private final String host;
	private final String signStr;
	private final Serializer serializer;
	private final Charset charset;
	
	public HttpRPCBean(BeanFactory beanFactory, Class<?> interfaceClass, String host, String signStr, Serializer serializer, Charset charset) throws Exception{
		super(beanFactory, interfaceClass);
		this.host = host;
		this.signStr = signStr;
		this.serializer = serializer;
		this.charset = charset;
	}

	public boolean isProxy() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		Object newProxyInstance = Proxy.newProxyInstance(getType().getClassLoader(),
				new Class[] { getType() }, new InvocationHandler() {

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						HttpConsumerInvoker httpConsumerInvoker = new HttpConsumerInvoker(host, method, signStr, serializer, charset);
						return httpConsumerInvoker.invoke(args);
					}
				});
		return (T) newProxyInstance;
	}

	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		throw new NotSupportException(getType().getName());
	}
}

class HttpConsumerInvoker implements Invoker{
	private Method method;
	private String host;
	private Class<?> returnType;
	private Charset charset;
	private String signStr;
	private Serializer serializer;
	
	public HttpConsumerInvoker(String host, Method method, String signStr, Serializer serializer, Charset charset){
		this.method = method;
		this.host = host;
		this.returnType = method.getReturnType();
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
