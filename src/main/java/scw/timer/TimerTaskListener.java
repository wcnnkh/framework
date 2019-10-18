package scw.timer;

public interface TimerTaskListener {
	void begin(TimerTaskContext timerTaskContext, long executionTime);

	void success(TimerTaskContext timerTaskContext, long executionTime);

	void error(TimerTaskContext timerTaskContext, long executionTime, Throwable e);

	void complete(TimerTaskContext timerTaskContext, long executionTime);
}
