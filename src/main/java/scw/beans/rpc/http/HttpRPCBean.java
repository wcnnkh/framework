package scw.beans.rpc.http;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

import scw.common.ByteArray;
import scw.common.reflect.Invoker;
import scw.common.utils.IOUtils;
import scw.common.utils.SignUtils;
import scw.net.http.HttpPost;
import scw.net.http.entity.JavaObjectRequestEntity;

public class HttpRPCBean extends AbstractInterfaceProxyBean {
	private final String host;
	private final String signStr;
	private final Charset charset;

	public HttpRPCBean(Class<?> interfaceClass, String host, String signStr, Charset charset) throws Exception {
		super(interfaceClass);
		this.host = host;
		this.signStr = signStr;
		this.charset = charset;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		Object newProxyInstance = Proxy.newProxyInstance(getType().getClassLoader(), new Class[] { getType() },
				new InvocationHandler() {

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						HttpConsumerInvoker httpConsumerInvoker = new HttpConsumerInvoker(host, method, signStr,
								charset);
						return httpConsumerInvoker.invoke(args);
					}
				});
		return (T) newProxyInstance;
	}
}

class HttpConsumerInvoker implements Invoker {
	private Method method;
	private String host;
	private Charset charset;
	private String signStr;

	public HttpConsumerInvoker(String host, Method method, String signStr, Charset charset) {
		this.method = method;
		this.host = host;
		this.charset = charset;
		this.signStr = signStr;
	}

	public Object invoke(Object... args) throws Exception {
		long cts = System.currentTimeMillis();
		Message message = new Message(method, args);
		message.setAttribute("t", cts);
		message.setAttribute("sign", SignUtils.md5Str(cts + signStr, charset.name()));
		HttpPost http = null;
		JavaObjectRequestEntity requestEntity = new JavaObjectRequestEntity();
		requestEntity.add(message);
		try {
			http = new HttpPost(host);
			http.setRequestEntity(requestEntity);

			ByteArray byteArray = http.getResponseByteArray();
			byte[] data = byteArray.toByteArray();
			if (data == null) {
				return null;
			}
			return IOUtils.byteToJavaObject(data);
		} finally {
			if (http != null) {
				http.disconnect();
			}
		}
	}
}
