package io.basc.framework.rpc.remote.web;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.SignerRemoteMessageCodec;

@Provider
public class WebBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final String RPC_HTTP_SIGN_NAME = "rpc.http.sign";
	private static final String RPC_HTTP_PATH = "mvc.http.rpc-path";

	private static final String WEB_RPC_SECRET_KEY = "web.rpc.secret.key";
	private static final String WEB_RPC_PATH = "web.rpc.path";
	private static final String DEFAULT_RPC_PATH = "/rpc";
	
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		RemoteHttpServiceHandlerDefinition httpServiceDefinition = new RemoteHttpServiceHandlerDefinition(
				beanFactory);
		if (!beanFactory.containsDefinition(httpServiceDefinition.getId())) {
			beanFactory.registerDefinition(httpServiceDefinition);
		}
	}
	
	private static class RemoteHttpServiceHandlerDefinition extends
			DefaultBeanDefinition {

		public RemoteHttpServiceHandlerDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, RemoteHttpServiceHandler.class);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.getEnvironment().containsKey(RPC_HTTP_SIGN_NAME)
					|| beanFactory.getEnvironment().containsKey(
							WEB_RPC_SECRET_KEY);
		}

		@Override
		public Object create() throws InstanceException {
			RemoteMessageCodec messageCodec;
			if (beanFactory.isInstance(RemoteMessageCodec.class)) {
				messageCodec = beanFactory
						.getInstance(RemoteMessageCodec.class);
			} else {
				String secretKey = beanFactory.getEnvironment().getValue(
						RPC_HTTP_SIGN_NAME,
						String.class,
						beanFactory.getEnvironment().getString(
								WEB_RPC_SECRET_KEY));
				messageCodec = new SignerRemoteMessageCodec(secretKey);
			}

			String path = beanFactory.getEnvironment().getValue(
					RPC_HTTP_PATH,
					String.class,
					beanFactory.getEnvironment().getValue(WEB_RPC_PATH,
							String.class, DEFAULT_RPC_PATH));
			return new RemoteHttpServiceHandler(beanFactory, messageCodec, path);
		}
	}

}
