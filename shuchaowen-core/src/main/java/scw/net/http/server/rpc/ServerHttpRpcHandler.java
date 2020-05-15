package scw.net.http.server.rpc;

import java.io.IOException;

import scw.core.instance.annotation.Configuration;
import scw.net.http.HttpMethod;
import scw.net.http.server.HttpServiceHandler;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.rpc.RpcService;

@Configuration(order = ServerHttpRpcHandler.ORDER)
public final class ServerHttpRpcHandler implements HttpServiceHandler {
	public static final int ORDER = 900;
	private final String rpcPath;
	private final RpcService rpcService;

	public ServerHttpRpcHandler(RpcService rpcService, String rpcPath) {
		this.rpcService = rpcService;
		this.rpcPath = rpcPath;
	}

	public boolean accept(ServerHttpRequest request) {
		if (!request.getController().equals(rpcPath)) {
			return false;
		}

		if (HttpMethod.POST != request.getMethod()) {
			return false;
		}
		return true;
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		rpcService.service(request, response);
	}
}
