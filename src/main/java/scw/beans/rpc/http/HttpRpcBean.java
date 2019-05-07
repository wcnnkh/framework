package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLConnection;
import java.nio.charset.Charset;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.aop.Invoker;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.net.AbstractResponse;
import scw.core.net.NetworkUtils;
import scw.core.net.http.ContentType;
import scw.core.net.http.HttpRequest;
import scw.core.serializer.Serializer;
import scw.core.utils.SignUtils;

public final class HttpRpcBean extends AbstractInterfaceProxyBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final String host;
	private final String signStr;
	private final Charset charset;
	private final BeanFactory beanFactory;
	private final Serializer serializer;

	public HttpRpcBean(BeanFactory beanFactory, Class<?> interfaceClass, String host, String signStr, Charset charset,
			Serializer serializer) throws Exception {
		super(interfaceClass);
		this.beanFactory = beanFactory;
		this.host = host;
		this.signStr = signStr;
		this.charset = charset;
		this.serializer = serializer;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
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
			final Message message = new Message(method, args);
			message.setAttribute("t", cts);
			message.setAttribute("sign", SignUtils.md5Str((cts + signStr).getBytes(charset)));

			HttpRequest request = new HttpRequest(scw.core.net.http.Method.POST, host) {
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
