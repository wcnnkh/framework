package scw.locks;

import java.util.concurrent.locks.Lock;

public interface LockFactory {
	Lock getLock(String name);
}
