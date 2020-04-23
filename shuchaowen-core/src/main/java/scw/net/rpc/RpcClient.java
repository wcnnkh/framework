package scw.net.rpc;

public interface RpcClient {
	Object getProxy(Class<?> interfaceClass);
}
