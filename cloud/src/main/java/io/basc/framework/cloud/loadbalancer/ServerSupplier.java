package io.basc.framework.cloud.loadbalancer;

import java.util.List;


public interface ServerSupplier<T> {
	List<Server<T>> getServers();
}
