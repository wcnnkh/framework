package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLConnection;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.io.Bytes;
import scw.io.serializer.Serializer;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.AbstractResponse;
import scw.net.ContentType;
import scw.net.NetworkUtils;
import scw.net.http.HttpRequest;
import scw.security.signature.SignatureUtils;

public final class HttpRpcBean extends AbstractInterfaceBeanDefinition {
	private Logger logger = LoggerUtils.getLogger(getClass());
	private final String host;
	private final String signStr;
	private final Serializer serializer;
	private final boolean responseThrowable;// 是否返回异常

	public HttpRpcBean(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, String host, String signStr, Serializer serializer, boolean responsethrowable) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		this.host = host;
		this.signStr = signStr;
		this.serializer = serializer;
		this.responseThrowable = responsethrowable;
		init();
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		Filter filter = new Filter() {

			public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
					throws Throwable {
				HttpConsumerInvoker httpConsumerInvoker = new HttpConsumerInvoker(method);
				return httpConsumerInvoker.invoke(args);
			}
		};
		return (T) BeanUtils.proxyInterface(beanFactory, getType(), filter);
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
			message.setAttribute("sign",
					(SignatureUtils.byte2hex(SignatureUtils.md5(Bytes.string2bytes(cts + signStr)))));
			message.setAttribute("responseThrowable", responseThrowable);

			HttpRequest request = new HttpRequest(scw.net.http.Method.POST, host) {
				@Override
				protected void doOutput(URLConnection urlConnection, OutputStream os) throws Throwable {
					serializer.serialize(os, message);
				}
			};
			request.setContentType(ContentType.APPLICATION_OCTET_STREAM);

			Object responseObject = null;
			try {
				responseObject = NetworkUtils.execute(request, new AbstractResponse<Object>() {

					@Override
					protected Object doInput(URLConnection urlConnection, InputStream is) throws Throwable {
						return serializer.deserialize(is);
					}
				});
			} catch (Throwable e) {
				logger.error(message.getMessageKey());
				throw e;
			}

			if (responseObject == null) {
				return null;
			}

			if (responseObject instanceof HttpRcpResponse) {
				Throwable throwable = ((HttpRcpResponse) responseObject).getThrowable();
				if (throwable != null) {
					logger.error(message.getMessageKey());
					throw throwable;
				}
				return ((HttpRcpResponse) responseObject).getResponse();
			}
			return responseObject;
		}
	}
}
