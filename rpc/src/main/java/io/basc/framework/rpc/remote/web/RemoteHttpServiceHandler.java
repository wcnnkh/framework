package io.basc.framework.rpc.remote.web;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.MediaType;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.remote.DefaultRemoteResponseMessage;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.RemoteMessageCodecException;
import io.basc.framework.rpc.remote.RemoteRequestMessage;
import io.basc.framework.rpc.remote.RemoteResponseMessage;
import io.basc.framework.rpc.support.ServiceCallableFactory;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.pattern.HttpPattern;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * 依赖web模块
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public class RemoteHttpServiceHandler extends HttpPattern implements HttpService {
	private static Logger logger = LoggerFactory.getLogger(RemoteHttpServiceHandler.class);
	private final CallableFactory callableFactory;
	private final RemoteMessageCodec messageCodec;

	public RemoteHttpServiceHandler(NoArgsInstanceFactory instanceFactory, RemoteMessageCodec messageCodec,
			String path) {
		this(new ServiceCallableFactory(instanceFactory), messageCodec, path);
	}

	public RemoteHttpServiceHandler(CallableFactory callableFactory, RemoteMessageCodec messageCodec, String path) {
		super(path, HttpMethod.POST.name());
		this.callableFactory = callableFactory;
		this.messageCodec = messageCodec;
	}

	public void service(final ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		DefaultRemoteResponseMessage responseMessage = new DefaultRemoteResponseMessage();
		RemoteRequestMessage requestMessage = null;
		try {
			requestMessage = (RemoteRequestMessage) messageCodec.decode(request);
		} catch (RemoteMessageCodecException e) {
			responseMessage.setThrowable(e);
			logger.error(e, "message decode error");
			response(response, responseMessage, requestMessage);
			return;
		}

		Callable<Object> callable = callableFactory.getCallable(requestMessage.getTargetClass(),
				requestMessage.getMethod(), requestMessage.getArgs());

		try {
			Object obj = callable.call();
			responseMessage.setBody(obj);
		} catch (Exception e) {
			responseMessage.setThrowable(e);
			logger.error(e, requestMessage.toString());
		}
		response(response, responseMessage, requestMessage);
	}

	private void response(final ServerHttpResponse response, RemoteResponseMessage responseMessage,
			RemoteRequestMessage requestMessage) throws RemoteMessageCodecException, IOException {
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		messageCodec.encode(response, responseMessage, requestMessage);
	}
}
