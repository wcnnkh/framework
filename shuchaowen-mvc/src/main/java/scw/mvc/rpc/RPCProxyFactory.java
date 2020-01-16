package scw.mvc.rpc;

public interface RPCProxyFactory {
	<T> T getProxy(Class<T> clazz);
}