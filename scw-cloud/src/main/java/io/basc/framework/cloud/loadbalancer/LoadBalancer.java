package io.basc.framework.cloud.loadbalancer;

public interface LoadBalancer<T> {
	Server<T> choose(ServerAccept<T> accept);

	void stat(Server<T> server, State state);
}
