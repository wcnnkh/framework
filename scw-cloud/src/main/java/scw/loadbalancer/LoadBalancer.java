package scw.loadbalancer;

import scw.aop.annotation.AopEnable;

@AopEnable(false)
public interface LoadBalancer<T> {
	Server<T> choose(ServerAccept<T> accept);

	void stat(Server<T> server, State state);
}
