package scw.cloud.loadbalancer;

import scw.util.Accept;

@FunctionalInterface
public interface ServerAccept<T> extends Accept<Server<T>>{
	boolean accept(Server<T> server);
}
