package scw.utils.locks;

public interface LockFactory {
	Lock getLock(String name);
	
	Lock getLock(String name, int timeout);
}
