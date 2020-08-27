package scw.rpc.http;

import scw.aop.MethodInvoker;
import scw.http.client.ClientHttpRequest;

public interface HttpRpcProxyRequestFactory {
	ClientHttpRequest getClientHttpRequest(MethodInvoker invoker, Object[] args) throws Exception;
}
