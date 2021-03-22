package scw.cloud.loadbalancer;

public interface ServerAccept<T> {
	boolean accept(Server<T> server);
}
