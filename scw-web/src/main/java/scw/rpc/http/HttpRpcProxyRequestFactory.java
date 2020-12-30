package scw.rpc.http;

import scw.core.reflect.MethodInvoker;
import scw.http.client.ClientHttpRequest;

public interface HttpRpcProxyRequestFactory {
	ClientHttpRequest getClientHttpRequest(MethodInvoker invoker, Object[] args) throws Exception;
}
