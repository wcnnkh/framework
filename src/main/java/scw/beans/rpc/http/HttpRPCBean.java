package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

import scw.beans.BeanFactory;
import scw.beans.rpc.transaction.TCCManager;
import scw.common.reflect.Invoker;
import scw.common.utils.SignUtils;
import scw.common.utils.XUtils;
import scw.net.AbstractResponse;
import scw.net.NetworkUtils;
import scw.net.http.request.HttpRequest;

public class HttpRPCBean extends AbstractInterfaceProxyBean {
	private final String host;
	private final String signStr;
	private final Charset charset;
	private final BeanFactory beanFactory;

	public HttpRPCBean(BeanFactory beanFactory, Class<?> interfaceClass, String host, String signStr, Charset charset)
			throws Exception {
		super(interfaceClass);
		this.beanFactory = beanFactory;
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
		return (T) TCCManager.convertTransactionProxy(beanFactory, getType(), newProxyInstance);
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
		final Message message = new Message(method, args);
		message.setAttribute("t", cts);
		message.setAttribute("sign", SignUtils.md5Str(cts + signStr, charset.name()));

		HttpRequest request = new HttpRequest(scw.net.http.enums.Method.POST) {
			@Override
			public void doOutput(OutputStream os) throws Throwable {
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(message);
			}
		};

		return NetworkUtils.executeHttp(host, request, new AbstractResponse<Object>() {

			@Override
			public Object doInput(InputStream is) throws Throwable {
				ObjectInputStream ois = null;
				try {
					ois = new ObjectInputStream(is);
					return ois.readObject();
				} finally {
					XUtils.close(ois);
				}
			}
		});
	}
}
