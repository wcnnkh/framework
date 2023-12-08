package io.basc.framework.observe.mode;

import io.basc.framework.observe.Observer;

/**
 * 观察者模式
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public abstract class ObserverMode<E> extends Observer<E> implements Viewer {

	/**
	 * 是否是拉模式
	 * 
	 * @return
	 */
	public boolean isPullMode() {
		return hasTimerTask();
	}

	/**
	 * 是否是推模式
	 * 
	 * @return
	 */
	public boolean isPushMode() {
		return hasEndlessLoop();
	}

	/**
	 * 启动拉模式Watcher
	 * 
	 * @return
	 */
	public boolean startPullMode() {
		return startTimerTask(this);
	}

	/**
	 * 启动推模式Watcher
	 * 
	 * @return
	 */
	public boolean startPushMode() {
		return startEndlessLoop(() -> {
			try {
				await();
			} catch (InterruptedException e) {
				return;
			}
			run();
		});
	}

	public boolean stopPullMode() {
		return stopTimerTask();
	}

	public boolean stopPushMode() {
		return stopEndlessLoop();
	}

	@Override
	public void await() throws InterruptedException {
		while (!await(getListenerCount(), getRefreshTimeUnit())) {
		}
	}
}
