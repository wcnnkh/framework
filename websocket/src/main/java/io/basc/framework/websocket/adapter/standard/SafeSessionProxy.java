package io.basc.framework.websocket.adapter.standard;

import io.basc.framework.util.Assert;
import io.basc.framework.util.collection.ArrayUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

/**
 * 代理一个session,以保证对客户端的写操作是线程安全的
 * 
 * @see Session#getBasicRemote()
 * @see Session#close()
 * @see Session#close(javax.websocket.CloseReason)
 * @author wcnnkh
 *
 */
public interface SafeSessionProxy extends Session {
	Session getTargetSession();

	public static SafeSessionProxy proxy(Session session) {
		Assert.requiredArgument(session != null, "session");
		
		if (session instanceof SafeSessionProxy) {
			return (SafeSessionProxy) session;
		}

		return (SafeSessionProxy) Proxy.newProxyInstance(SafeSessionProxy.class.getClassLoader(),
				new Class<?>[] { SafeSessionProxy.class }, new SafeSessionInvocationHandler(session));
	}

	static class SafeSessionInvocationHandler implements InvocationHandler {
		private final Session session;
		private volatile Object basic;

		public SafeSessionInvocationHandler(Session session) {
			this.session = session;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (ArrayUtils.isEmpty(args)) {
				if (method.getName().equals("getTargetSession")) {
					return session;
				}

				if (method.getName().equals("getBasicRemote")) {
					if (basic == null) {
						synchronized (session) {
							if (basic == null) {
								Object remoteEndpoint = method.invoke(session, args);
								basic = Proxy.newProxyInstance(SafeSessionProxy.class.getClassLoader(),
										new Class<?>[] { RemoteEndpoint.Basic.class },
										new SafeRemoteEndpoint(remoteEndpoint));
							}
						}
					}
					return basic;
				}
			}

			if (method.getName().equals("close")) {
				synchronized (session) {
					return method.invoke(session, args);
				}
			}

			return method.invoke(session, args);
		}

		private class SafeRemoteEndpoint implements InvocationHandler {
			private final Object remoteEndpoint;

			private SafeRemoteEndpoint(Object remoteEndpoint) {
				this.remoteEndpoint = remoteEndpoint;
			}

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getName().startsWith("send")) {
					synchronized (session) {
						return method.invoke(remoteEndpoint, args);
					}
				}
				return method.invoke(remoteEndpoint, args);
			}

		}
	}
}
