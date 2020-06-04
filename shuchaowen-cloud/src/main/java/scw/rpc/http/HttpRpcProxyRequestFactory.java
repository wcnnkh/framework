package scw.rpc.http;

import scw.aop.ProxyInvoker;
import scw.http.client.ClientHttpRequest;

public interface HttpRpcProxyRequestFactory {
	ClientHttpRequest getClientHttpRequest(ProxyInvoker invoker, Object[] args) throws Exception;
}
