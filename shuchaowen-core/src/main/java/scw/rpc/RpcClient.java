package scw.rpc;

public interface RpcClient {
	Object getProxy(Class<?> interfaceClass);
}
