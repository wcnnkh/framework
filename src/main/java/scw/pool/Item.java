package scw.pool;

public interface Item {
	boolean isAvailable();
	
	void reset();
	
	void destroy();
}
