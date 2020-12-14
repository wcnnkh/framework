package scw.loadbalancer;

import java.util.List;


public interface ServerSupplier<T> {
	List<Server<T>> getServers();
}
