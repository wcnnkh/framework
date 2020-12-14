package scw.loadbalancer;

public interface Server<T>{
	String getId();
	
	int getWeight();
	
	T getService();
}
