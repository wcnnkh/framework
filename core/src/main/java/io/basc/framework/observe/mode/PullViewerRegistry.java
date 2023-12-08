package io.basc.framework.observe.mode;

/**
 * pull模式
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public class PullViewerRegistry<E extends PullViewer> extends ViewerRegistry<E> {

	public boolean start() {
		return startTimerTask(this);
	}

	public boolean stop() {
		return stopTimerTask();
	}
}
