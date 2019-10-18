package scw.timer;

public interface TimerTaskContext {
	String getId();

	boolean cancel();

	TimerTask getTimerTask();
}
