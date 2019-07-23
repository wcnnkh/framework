package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLConnection;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.rpc.AbstractInterfaceProxyBean;
import scw.core.aop.Invoker;
import scw.core.io.Bytes;
import scw.core.io.serializer.Serializer;
import scw.core.logger.Logger;
import scw.core.utils.SignUtils;
import scw.logger.LoggerFactory;
import scw.net.AbstractResponse;
import scw.net.ContentType;
import scw.net.NetworkUtils;
import scw.net.http.HttpRequest;

public final class HttpRpcBean extends AbstractInterfaceProxyBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final String host;
	private final String signStr;
	private final BeanFactory beanFactory;
	private final Serializer serializer;

	public HttpRpcBean(BeanFactory beanFactory, Class<?> interfaceClass, String host, String signStr,
			Serializer serializer) throws Exception {
		super(interfaceClass);
		this.beanFactory = beanFactory;
		this.host = host;
		this.signStr = signStr;
		this.serializer = serializer;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		Object newProxyInstance = Proxy.newProxyInstance(getType().getClassLoader(), new Class[] { getType() },
				new InvocationHandler() {

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						HttpConsumerInvoker httpConsumerInvoker = new HttpConsumerInvoker(method);
						return httpConsumerInvoker.invoke(args);
					}
				});
		return (T) BeanUtils.proxyInterface(beanFactory, getType(), newProxyInstance);
	}

	final class HttpConsumerInvoker implements Invoker {
		private Method method;

		public HttpConsumerInvoker(Method method) {
			this.method = method;
		}

		public Object invoke(Object... args) throws Throwable {
			long cts = System.currentTimeMillis();
			final Message message = new Message(getType(), method, args);
			message.setAttribute("t", cts);
			message.setAttribute("sign", SignUtils.md5Str(Bytes.string2bytes(cts + signStr)));

			HttpRequest request = new HttpRequest(scw.net.http.Method.POST, host) {
				@Override
				protected void doOutput(URLConnection urlConnection, OutputStream os) throws Throwable {
					serializer.serialize(os, message);
				}
			};
			request.setContentType(ContentType.APPLICATION_OCTET_STREAM);

			try {
				return NetworkUtils.execute(request, new AbstractResponse<Object>() {

					@Override
					protected Object doInput(URLConnection urlConnection, InputStream is) throws Throwable {
						return serializer.deserialize(is);
					}
				});
			} catch (Throwable e) {
				logger.error(message.getMessageKey());
				throw e;
			}
		}
	}
}
