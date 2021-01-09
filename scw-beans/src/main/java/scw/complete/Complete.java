package scw.complete;

public interface Complete extends Runnable {
	void cancel();

	boolean isCancel();
	
	/**
	 * 立刻执行
	 */
	void run();
}
