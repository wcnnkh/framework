package io.basc.framework.rpc.remote.web;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.SignerRemoteMessageCodec;

@Provider
public class WebRpcContextPostProcessor implements ContextPostProcessor {
	private static final String RPC_HTTP_SIGN_NAME = "rpc.http.sign";
	private static final String RPC_HTTP_PATH = "mvc.http.rpc-path";

	private static final String WEB_RPC_SECRET_KEY = "web.rpc.secret.key";
	private static final String WEB_RPC_PATH = "web.rpc.path";
	private static final String DEFAULT_RPC_PATH = "/rpc";

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		RemoteHttpServiceHandlerDefinition httpServiceDefinition = new RemoteHttpServiceHandlerDefinition(context);
		if (!context.containsDefinition(httpServiceDefinition.getId())) {
			context.registerDefinition(httpServiceDefinition);
		}
	}

	private static class RemoteHttpServiceHandlerDefinition extends EnvironmentBeanDefinition {

		public RemoteHttpServiceHandlerDefinition(Environment environment) {
			super(environment, RemoteHttpServiceHandler.class);
		}

		@Override
		public boolean isInstance() {
			return getEnvironment().getProperties().containsKey(RPC_HTTP_SIGN_NAME)
					|| getEnvironment().getProperties().containsKey(WEB_RPC_SECRET_KEY);
		}

		@Override
		public Object create() throws InstanceException {
			RemoteMessageCodec messageCodec;
			if (getBeanFactory().isInstance(RemoteMessageCodec.class)) {
				messageCodec = getBeanFactory().getInstance(RemoteMessageCodec.class);
			} else {
				String secretKey = getEnvironment().getProperties().get(RPC_HTTP_SIGN_NAME)
						.or(getEnvironment().getProperties().getAsString(WEB_RPC_SECRET_KEY)).getAsString();
				messageCodec = new SignerRemoteMessageCodec(secretKey);
			}

			String path = getEnvironment().getProperties().get(RPC_HTTP_PATH)
					.orElse(getEnvironment().getProperties().get(WEB_RPC_PATH).or(DEFAULT_RPC_PATH)).getAsString();
			return new RemoteHttpServiceHandler(getEnvironment(), messageCodec, path);
		}
	}

}
