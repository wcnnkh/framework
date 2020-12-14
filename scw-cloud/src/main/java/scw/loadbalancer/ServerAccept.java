package scw.loadbalancer;

public interface ServerAccept<T> {
	boolean accept(Server<T> server);
}
