package scw.mvc.rpc;

public interface RpcFactory {
	<T> T getProxy(Class<T> clazz);
}