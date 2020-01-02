package scw.rpc;

public interface RpcFactory {
	<T> T getProxy(Class<T> clazz);
}