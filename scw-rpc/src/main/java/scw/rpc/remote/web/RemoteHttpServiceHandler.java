package scw.rpc.remote.web;

import java.io.IOException;
import java.util.concurrent.Callable;

import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.http.server.HttpService;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.http.server.pattern.HttpPattern;
import scw.instance.NoArgsInstanceFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.rpc.CallableFactory;
import scw.rpc.remote.DefaultRemoteResponseMessage;
import scw.rpc.remote.RemoteMessageCodec;
import scw.rpc.remote.RemoteMessageCodecException;
import scw.rpc.remote.RemoteRequestMessage;
import scw.rpc.remote.RemoteResponseMessage;
import scw.rpc.support.ServiceCallableFactory;

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
		super(path, HttpMethod.POST);
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
