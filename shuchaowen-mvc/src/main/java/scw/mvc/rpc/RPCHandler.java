package scw.mvc.rpc;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.beans.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.Request;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;
import scw.mvc.http.HttpRequest;
import scw.net.MimeTypeUtils;
import scw.net.http.HttpMethod;
import scw.util.value.property.PropertyFactory;

@Configuration(order=RPCHandler.ORDER)
public final class RPCHandler implements Handler{
	public static final int ORDER = 900;
	
	private static Logger logger = LoggerUtils.getLogger(RPCHandler.class);
	private final String rpcPath;
	private final RpcService rpcService;
	
	public RPCHandler(PropertyFactory propertyFactory, BeanFactory beanFactory){
		this.rpcService = MVCUtils.getRpcService(propertyFactory, beanFactory);
		this.rpcPath = MVCUtils.getRPCPath(propertyFactory);
	}
	
	public RPCHandler(RpcService rpcService, String rpcPath){
		this.rpcService = rpcService;
		this.rpcPath = rpcPath;
	}
	
	public Object doHandler(Channel channel, HandlerChain chain) throws Throwable {
		if (checkRPCEnable(channel.getRequest())) {
			channel.getResponse().setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
			try {
				rpcService.service(channel.getRequest().getBody(), channel.getResponse().getBody());
			} catch (IOException e) {
				logger.error(e, channel.toString());
			}
			return  null;
		}
		
		return chain.doHandler(channel);
	}
	
	protected final boolean checkRPCEnable(Request request) {
		if (rpcService == null) {
			return false;
		}

		if (!request.getControllerPath().equals(rpcPath)) {
			return false;
		}
		
		if(request instanceof HttpRequest){
			if (HttpMethod.POST != ((HttpRequest)request).getMethod()) {
				return false;
			}
		}
		return StringUtils.startsWith(request.getRawContentType(), MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, true);
	}

}
