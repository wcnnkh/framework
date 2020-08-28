package scw.locks;

import java.util.concurrent.TimeUnit;

public interface LockFactory {
	Lock getLock(String name);

	Lock getLock(String name, long timeout, TimeUnit timeUnit);
}
