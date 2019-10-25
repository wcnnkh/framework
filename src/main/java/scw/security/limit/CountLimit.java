package scw.security.limit;

public interface CountLimit {
	boolean incr();
	
	long getCount();
	
	void reset();
}
