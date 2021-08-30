package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.util.Accept;

@FunctionalInterface
public interface ServerAccept<T> extends Accept<Server<T>>{
	boolean accept(Server<T> server);
}
