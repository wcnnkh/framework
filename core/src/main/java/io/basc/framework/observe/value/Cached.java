package io.basc.framework.observe.value;

import io.basc.framework.observe.Pull;
import io.basc.framework.observe.mode.PullViewer;

/**
 * 拉模式
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public abstract class Cached<T extends Pull> extends AbstractObservableValue<T> implements PullViewer {

	@Override
	public void run() {
		// CAS
		setLastModified(lastModified(), map((e) -> e.lastModified()).orElse(0L));
	}

	public boolean start() {
		return startTimerTask(this);
	}

	public boolean stop() {
		return stopTimerTask();
	}

	@Override
	public void await() throws InterruptedException {
		while (!await(getListenerCount(), getRefreshTimeUnit())) {
		}
	}
}
