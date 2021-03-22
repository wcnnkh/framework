package scw.cloud.loadbalancer;

import java.util.List;


public interface ServerSupplier<T> {
	List<Server<T>> getServers();
}
