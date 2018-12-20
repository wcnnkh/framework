package scw.locks;

public abstract class AbstractLock implements Lock{
	public void lockWait(long millis, int nanos) throws InterruptedException {
		while (!lock()) {
			Thread.sleep(millis, nanos);
		}
	}
}
