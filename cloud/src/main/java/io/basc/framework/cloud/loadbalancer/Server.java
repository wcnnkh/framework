package io.basc.framework.cloud.loadbalancer;

public interface Server<T>{
	String getId();
	
	int getWeight();
	
	T getService();
}
