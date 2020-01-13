package scw.rpc.support;

import java.util.Collection;

import scw.http.client.HttpMessageConverterExtractor;
import scw.http.converter.HttpMessageConverter;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.rpc.RequestMessage;
import scw.rpc.RpcService;

//TODO
public class HttpRpcService {
	private RpcService rpcService;
	private Collection<HttpMessageConverter<?>> httpMessageConverters;

	public HttpRpcService(RpcService rpcService, Collection<HttpMessageConverter<?>> httpMessageConverters) {
		this.rpcService = rpcService;
		this.httpMessageConverters = httpMessageConverters;
	}

	public void service(ServerHttpRequest httpRequest, ServerHttpResponse httpResponse) {
	}
}
