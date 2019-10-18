package scw.timer.refresh;

import java.util.concurrent.TimeUnit;

public interface Refresh<V> {
	V getValue();

	void refresh() throws InterruptedException;

	void refresh(TimeUnit timeUnit, long timeout) throws InterruptedException;
}
