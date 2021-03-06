package scw.rpc.web;

import java.io.IOException;
import java.util.concurrent.Callable;

import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.http.server.HttpServiceHandler;
import scw.http.server.HttpServiceHandlerControllerDesriptor;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.instance.NoArgsInstanceFactory;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.rpc.CallableFactory;
import scw.rpc.messageing.MessageHeaders;
import scw.rpc.messageing.RemoteMessageCodec;
import scw.rpc.messageing.RemoteMethodRequestMessage;
import scw.rpc.messageing.RemoteRequestMessage;
import scw.rpc.messageing.support.DefaultMessageHeaders;
import scw.rpc.messageing.support.DefaultRemoteResponseMessage;
import scw.rpc.support.ServiceCallableFactory;

/**
 * 依赖web模块
 * @author shuchaowen
 *
 */
@Provider(order=Ordered.HIGHEST_PRECEDENCE)
public class RemoteHttpServiceHandler implements HttpServiceHandler,
		HttpServiceHandlerControllerDesriptor {
	private static Logger logger = LoggerFactory
			.getLogger(RemoteHttpServiceHandler.class);
	private final CallableFactory callableFactory;
	private final RemoteMessageCodec messageCodec;
	private final HttpControllerDescriptor controllerDescriptor;
	
	public RemoteHttpServiceHandler(NoArgsInstanceFactory instanceFactory, RemoteMessageCodec messageCodec, String path){
		this(new ServiceCallableFactory(instanceFactory), messageCodec, path);
	}

	public RemoteHttpServiceHandler(CallableFactory callableFactory,
			RemoteMessageCodec messageCodec, String path) {
		this.callableFactory = callableFactory;
		this.messageCodec = messageCodec;
		this.controllerDescriptor = new HttpControllerDescriptor(path,
				HttpMethod.POST);
	}

	public HttpControllerDescriptor getHttpControllerDescriptor()  {
		return controllerDescriptor;
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		MessageHeaders messageHeaders = new DefaultMessageHeaders();
		RemoteRequestMessage requestMessage = messageCodec.decode(
				request.getBody(), messageHeaders);
		if (!(requestMessage instanceof RemoteMethodRequestMessage)) {
			throw new NotSupportedException(requestMessage.toString());
		}

		RemoteMethodRequestMessage message = (RemoteMethodRequestMessage) requestMessage;
		Callable<Object> callable = callableFactory.getCallable(
				message.getTargetClass(), message.getMethod(),
				message.getArgs());

		DefaultRemoteResponseMessage responseMessage = new DefaultRemoteResponseMessage();
		try {
			Object obj = callable.call();
			responseMessage.setBody(obj);
		} catch (Exception e) {
			responseMessage.setThrowable(e);
			logger.error(e, requestMessage.toString());
		}
		messageCodec.encode(response.getBody(), responseMessage);
	}
}
