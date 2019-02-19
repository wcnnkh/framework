package scw.locks;

public interface LockFactory {
	Lock getLock(String name);
	
	Lock getLock(String name, int timeout);
}
