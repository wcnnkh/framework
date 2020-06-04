package scw.rpc.simple.http;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.http.server.HttpServiceHandler;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.rpc.RpcService;
import scw.value.property.PropertyFactory;

@Configuration(order=Integer.MAX_VALUE)
public final class HttpServerRpcHandler implements HttpServiceHandler {
	private final String rpcPath;
	private final RpcService rpcService;

	public HttpServerRpcHandler(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		String path = propertyFactory.getString("mvc.http.rpc-path");
		this.rpcPath = StringUtils.isEmpty(path) ? "/rpc" : path;
		this.rpcService = beanFactory.isInstance(RpcService.class) ? beanFactory.getInstance(RpcService.class) : null;
	}

	public HttpServerRpcHandler(RpcService rpcService, String rpcPath) {
		this.rpcService = rpcService;
		this.rpcPath = rpcPath;
	}

	public boolean accept(ServerHttpRequest request) {
		if (rpcService == null) {
			return false;
		}

		if (!request.getPath().equals(rpcPath)) {
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
