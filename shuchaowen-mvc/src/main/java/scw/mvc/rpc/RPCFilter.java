package scw.mvc.rpc;

import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.service.Filter;
import scw.mvc.service.FilterChain;
import scw.net.http.HttpMethod;
import scw.net.http.server.ServerHttpRequest;
import scw.net.rpc.RpcService;
import scw.value.property.PropertyFactory;

@Configuration(order = RPCFilter.ORDER)
public final class RPCFilter implements Filter {
	public static final int ORDER = 900;
	private final String rpcPath;
	private final RpcService rpcService;

	public RPCFilter(PropertyFactory propertyFactory, RpcService rpcService) {
		this.rpcService = rpcService;
		this.rpcPath = MVCUtils.getRPCPath(propertyFactory);
	}

	public RPCFilter(RpcService rpcService, String rpcPath) {
		this.rpcService = rpcService;
		this.rpcPath = rpcPath;
	}

	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		if (checkRPCEnable(channel.getRequest())) {
			rpcService.service(channel.getRequest(), channel.getResponse());
			return null;
		}

		return chain.doFilter(channel);
	}

	protected final boolean checkRPCEnable(ServerHttpRequest serverRequest) {
		if (!serverRequest.getController().equals(rpcPath)) {
			return false;
		}

		if (serverRequest instanceof ServerHttpRequest) {
			if (HttpMethod.POST != ((ServerHttpRequest) serverRequest).getMethod()) {
				return false;
			}
		}
		return true;
	}

}
