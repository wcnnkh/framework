package scw.rpc.http;

import scw.aop.ProxyContext;
import scw.http.client.ClientHttpRequest;

public interface HttpRpcProxyRequestFactory {
	ClientHttpRequest getClientHttpRequest(ProxyContext proxyContext) throws Exception;
}
